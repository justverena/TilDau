package kz.kbtu.tildau.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.kbtu.tildau.dto.auth.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.SQLException;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CourseControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final String userId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void logUrl() throws SQLException {
        System.out.println("Testing DB URL: " + dataSource.getConnection().getMetaData().getURL());
    }

    @BeforeEach
    void setup() throws Exception {
        jdbcTemplate.execute("TRUNCATE TABLE exercises CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE course_units CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE courses CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE user_defect_types CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE role CASCADE");

        jdbcTemplate.update("INSERT INTO role (id, name) VALUES (?, ?) ON CONFLICT DO NOTHING", 1, "admin");
        jdbcTemplate.update("INSERT INTO role (id, name) VALUES (?, ?) ON CONFLICT DO NOTHING", 2, "user");
        jdbcTemplate.update("INSERT INTO role (id, name) VALUES (?, ?) ON CONFLICT DO NOTHING", 3, "doctor");

        jdbcTemplate.update(
                "INSERT INTO users (id, name, email, password, role_id, created_at, updated_at) " +
                        "VALUES (?::uuid, ?, ?, ?, ?, now(), now())",
                "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
                "Jane",
                "jane@test.com",
                "$2a$10$F8eP66sC5W8G6EmFfnQE9OyfqUn9BFBoWgSracnVF.jnWHfxkeh16",
                2
        );

        jdbcTemplate.update(
                "INSERT INTO user_defect_types (user_id, defect_type_id) VALUES (?::uuid, ?)",
                "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
                2
        );
        jdbcTemplate.update(
                "INSERT INTO courses (id, defect_type_id, title, description, is_active, created_at) " +
                        "VALUES (?::uuid, ?, ?, ?, ?, now())",
                "11111111-1111-1111-1111-111111111111",
                2,
                "stuttering",
                "stuttering basics",
                true
        );
        jdbcTemplate.update(
                "INSERT INTO courses (id, defect_type_id, title, description, is_active, created_at) " +
                        "VALUES (?::uuid, ?, ?, ?, ?, now())",
                "22222222-2222-2222-2222-222222222222",
                1,
                "articulation",
                "articulation basics",
                true
        );
        jdbcTemplate.update(
                "INSERT INTO course_units (id, course_id, title, description, order_index, created_at) " +
                        "VALUES (?::uuid, ?::uuid, ?, ?, ?, now())",
                "33333333-3333-3333-3333-333333333333",
                "11111111-1111-1111-1111-111111111111",
                "unit 1",
                "first",
                1
        );
        jdbcTemplate.update(
                "INSERT INTO exercises (id, unit_id, exercise_type, title, instruction, expected_text, order_index, created_at) " +
                        "VALUES (?::uuid, ?::uuid, ?, ?, ?, ?, ?, now())",
                "44444444-4444-4444-4444-444444444444",
                "33333333-3333-3333-3333-333333333333",
                "READ_ALOUD",
                "read",
                "read aloud",
                "why do you cry billy, why billy why",
                1
        );
    }

    private String loginAndGetToken(String email, String password) throws Exception {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void getCourses_success_only_for_user_defect() throws Exception {
        String token = loginAndGetToken("jane@test.com", "password123");

        mockMvc.perform(get("/api/courses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].description").exists())
                .andExpect(jsonPath("$[0].units").doesNotExist());
    }

    @Test
    void getCourses_fails_without_token() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCourseById_success_with_units_and_exercises() throws Exception {
        String token = loginAndGetToken("jane@test.com", "password123");

        String courseId = "11111111-1111-1111-1111-111111111111";

        mockMvc.perform(get("/api/courses/{id}", courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.units").isArray())
                .andExpect(jsonPath("$.units[0].exercises").isArray())
                .andExpect(jsonPath("$.units[0].exercises[0].title").exists());
    }

    @Test
    void getCourseById_fails_when_course_of_other_defect() throws Exception {
        String token = loginAndGetToken("jane@test.com", "password123");

        String anotherCourseId = "22222222-2222-2222-2222-222222222222";

        mockMvc.perform(get("/api/courses/{id}", anotherCourseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}