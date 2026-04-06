package kz.kbtu.tildau.controller;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CourseControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void getCourses_success_only_for_user_defect() throws Exception {

        String token = loginAndGetToken();

        mockMvc.perform(get("/api/courses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(courseId.toString()));
    }

    @Test
    void getCourses_fails_without_token() throws Exception {

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCourseById_success_with_units_and_exercises() throws Exception {

        UUID exerciseId = insertExercise(unitId);
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/courses/{id}", courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.units[0].exercises").isArray());
    }

    @Test
    void getCourseById_fails_when_course_of_other_defect() throws Exception {

        String token = loginAndGetToken();

        mockMvc.perform(get("/api/courses/{id}", otherCourseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void startCourse_success_creates_progress_and_returns_exercise() throws Exception {

        insertExercise(unitId);

        String token = loginAndGetToken();

        mockMvc.perform(post("/api/courses/{id}/start", courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("EXERCISE"))
                .andExpect(jsonPath("$.id").exists());

        int courseProgressCount = courseProgressCount(userId, courseId);
        int unitProgressCount = unitProgressCount(userId, unitId);
        int totalUnits = totalUnitsCount(userId, courseId);

        assertEquals(1, totalUnits);
        assertEquals(1, courseProgressCount);
        assertEquals(1, unitProgressCount);
    }

    @Test
    void startCourse_resume_existing_course() throws Exception {

        insertExercise(unitId);
        insertProgress();

        String token = loginAndGetToken();

        mockMvc.perform(post("/api/courses/{id}/start", courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("EXERCISE"))
                .andExpect(jsonPath("$.id").exists());

        int courseProgressCount = courseProgressCount(userId, courseId);
        int unitProgressCount = unitProgressCount(userId, unitId);

        assertEquals(1, courseProgressCount);
        assertEquals(1, unitProgressCount);
    }

    @Test
    void startCourse_fails_when_course_of_other_defect() throws Exception {

        String token = loginAndGetToken();

        mockMvc.perform(post("/api/courses/{id}/start", otherCourseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void startCourse_fails_without_token() throws Exception {

        mockMvc.perform(post("/api/courses/{id}/start", courseId))
                .andExpect(status().isForbidden());
    }

    @Test
    void startCourse_fails_when_course_completed() throws Exception {

        insertExercise(unitId);
        insertProgress();
        markUnitCompleted();

        String token = loginAndGetToken();

        mockMvc.perform(post("/api/courses/{id}/start", courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

}