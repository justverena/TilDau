package kz.kbtu.tildau.controller;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import kz.kbtu.tildau.dto.auth.LoginRequest;
import kz.kbtu.tildau.dto.auth.RegisterRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.MinIOContainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected UUID userId;
    protected UUID courseId;
    protected UUID otherCourseId;
    protected UUID unitId;
    protected UUID otherUnitId;
    protected final String EMAIL = "jane@test.com";
    protected final String PASSWORD = "password123";

    static final MinIOContainer minioContainer =
            new MinIOContainer("minio/minio:latest")
                    .withUserName("minio")
                    .withPassword("minio123");

    static {
        minioContainer.start();
    }

    @DynamicPropertySource
    static void minioProperties(DynamicPropertyRegistry registry) {
        registry.add("minio.url", minioContainer::getS3URL);
        registry.add("minio.access-key", minioContainer::getUserName);
        registry.add("minio.secret-key", minioContainer::getPassword);
        registry.add("minio.url-expiration-minutes", () -> 60L);
    }
    @BeforeAll
    static void setupBucket() throws Exception {
        MinioClient client = MinioClient.builder()
                .endpoint(minioContainer.getS3URL())
                .credentials(
                        minioContainer.getUserName(),
                        minioContainer.getPassword()
                )
                .build();

        String bucket = "tildau";

        if (!client.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build())) {

            client.makeBucket(
                    MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    @BeforeEach
    void baseSetup() {

        generateIds();
        truncateTables();
        insertRoles();
        insertDefectTypes();
        try {
            insertUser();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        insertUserDefectType();
        insertCourses();
        insertUnits();
    }
    private void generateIds() {
        userId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        otherCourseId = UUID.randomUUID();
        unitId = UUID.randomUUID();
        otherUnitId = UUID.randomUUID();
    }

    private void truncateTables() {
        jdbcTemplate.execute("TRUNCATE TABLE user_unit_progress, user_course_progress, exercises, course_units, courses, user_defect_types, users, role CASCADE;");
    }

    private void insertRoles() {
        jdbcTemplate.update("INSERT INTO role (id, name) VALUES (1, 'admin') ON CONFLICT DO NOTHING");
        jdbcTemplate.update("INSERT INTO role (id, name) VALUES (2, 'user') ON CONFLICT DO NOTHING");
        jdbcTemplate.update("INSERT INTO role (id, name) VALUES (3, 'doctor') ON CONFLICT DO NOTHING");
    }

    private void insertDefectTypes() {
        jdbcTemplate.update("""
        INSERT INTO defect_types (id, name)
        VALUES (1, 'articulation')
        ON CONFLICT DO NOTHING
    """);

        jdbcTemplate.update("""
        INSERT INTO defect_types (id, name)
        VALUES (2, 'stuttering')
        ON CONFLICT DO NOTHING
    """);
    }
    private void insertUser() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setName("Jane");
        register.setEmail(EMAIL);
        register.setPassword(PASSWORD);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        userId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE email = ?",
                UUID.class,
                EMAIL
        );
    }

    private void insertUserDefectType() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_defect_types WHERE user_id = ?",
                Integer.class,
                userId
        );
        if (count == 0) {
            jdbcTemplate.update("""
            INSERT INTO user_defect_types (user_id, defect_type_id)
            VALUES (?::uuid, 2)
        """, userId);
        }
    }

    private void insertCourses() {
        jdbcTemplate.update("""
            INSERT INTO courses (id, defect_type_id, title, description, is_active, created_at)
            VALUES (?::uuid, 2, 'stuttering', 'stuttering basics', true, now())
        """, courseId);

        jdbcTemplate.update("""
            INSERT INTO courses (id, defect_type_id, title, description, is_active, created_at)
            VALUES (?::uuid, 1, 'articulation', 'articulation basics', true, now())
        """, otherCourseId);
    }

    private void insertUnits() {
        jdbcTemplate.update("""
            INSERT INTO course_units (id, course_id, title, description, order_index, created_at)
            VALUES (?::uuid, ?::uuid, 'unit 1', 'first', 1, now())
        """, unitId, courseId);

        jdbcTemplate.update("""
            INSERT INTO course_units (id, course_id, title, description, order_index, created_at)
            VALUES (?::uuid, ?::uuid, 'unit 11', 'like first', 1, now())
        """, otherUnitId, otherCourseId);
    }

    protected UUID insertExercise(UUID unitId, int orderIndex) {
        UUID exerciseId = UUID.randomUUID();
        jdbcTemplate.update("""
        INSERT INTO exercises (id, unit_id, exercise_type, title, instruction, expected_text, order_index, created_at)
        VALUES (?, ?, 'READ_ALOUD', 'title', 'instruction', 'text', ?, now())
    """,
                exerciseId,
                unitId,
                orderIndex
        );
        return exerciseId;
    }

    protected UUID insertRepeatAfterAudioExercise(UUID unitId, String referenceAudioUrl) {
        UUID exerciseId = UUID.randomUUID();

        jdbcTemplate.update("""
        INSERT INTO exercises (id, unit_id, exercise_type, title, instruction,
                               expected_text, reference_audio_url, order_index, created_at)
        VALUES (?, ?, 'REPEAT_AFTER_AUDIO', 'title', 'instruction',
                'Hidden text', ?, 1, now())
    """,
                exerciseId,
                unitId,
                referenceAudioUrl
        );

        return exerciseId;
    }

    void insertProgress(int totalExercises) {
        jdbcTemplate.update("""
        INSERT INTO user_course_progress (
            user_id, course_id,
            completed_units,
            total_units,
            progress_percent,
            started_at,
            finished_at
        )
        VALUES (?::uuid, ?::uuid, 0, 1, 0, now(), NULL)
    """, UUID.fromString(userId.toString()), UUID.fromString(courseId.toString()));

        jdbcTemplate.update("""
        INSERT INTO user_unit_progress (
            user_id, unit_id,
            completed_exercises,
            total_exercises,
            is_completed
        )
        VALUES (?::uuid, ?::uuid, 0, ?, false)
    """, UUID.fromString(userId.toString()), UUID.fromString(unitId.toString()), totalExercises);
    }

    protected int courseProgressCount(UUID userId, UUID courseId) {
        return jdbcTemplate.queryForObject("""
        SELECT COUNT(*) FROM user_course_progress
        WHERE user_id = ? AND course_id = ?
    """, Integer.class, userId, courseId);
    }
    protected int unitProgressCount(UUID userId, UUID unitId) {
        return jdbcTemplate.queryForObject("""
        SELECT COUNT(*) FROM user_unit_progress
        WHERE user_id = ? AND unit_id = ?
    """, Integer.class, userId, unitId);
    }

    protected int totalUnitsCount(UUID userId, UUID courseId) {
        return jdbcTemplate.queryForObject("""
        SELECT total_units FROM user_course_progress
        WHERE user_id = ? AND course_id = ?
    """, Integer.class, userId, courseId);
    }

    protected void markUnitCompleted() {
        jdbcTemplate.update("""
        UPDATE user_unit_progress
        SET is_completed = true
        WHERE user_id = ? AND unit_id = ?
    """, userId, unitId);
    }

    protected void markExerciseCompleted() {
        jdbcTemplate.update("""
        UPDATE user_unit_progress
        SET completed_exercises = 1
        WHERE user_id = ? AND unit_id = ?
    """, userId, unitId);
    }
    protected int getAttemptCount(UUID exerciseId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_exercises WHERE exercise_id = ?",
                Integer.class,
                exerciseId
        );
    }
    protected int completedExercisesCount(UUID userId, UUID unitId) {
        return jdbcTemplate.queryForObject("""
        SELECT completed_exercises FROM user_unit_progress
        WHERE user_id = ? AND unit_id = ?
    """, Integer.class, userId, unitId);
    }
    protected String loginAndGetToken() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }
}