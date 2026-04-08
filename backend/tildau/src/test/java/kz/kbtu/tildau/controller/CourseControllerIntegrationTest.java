package kz.kbtu.tildau.controller;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CourseControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void getCourses_success() throws Exception {

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
    void getCourseById_with_progress() throws Exception {

        UUID exerciseId = insertExercise(unitId, 1);
        insertProgress(1);

        String token = loginAndGetToken();

        mockMvc.perform(get("/api/courses/{id}", courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressPercent").value(0))
                .andExpect(jsonPath("$.units[0].completed").value(false));
    }

    @Test
    void getCourseById_success_with_units_and_exercises() throws Exception {

        UUID exerciseId = insertExercise(unitId, 1);
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/courses/{id}", courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressPercent").value(0))
                .andExpect(jsonPath("$.units[0].completed").value(false))
                .andExpect(jsonPath("$.units[0].exercises").isArray())
                .andExpect(jsonPath("$.units[0].exercises[0].completed").value(false))
                .andExpect(jsonPath("$.units[0].exercises[0].locked").value(false));
    }

    @Test
    void getCourseById_completed_exercise() throws Exception {

        UUID exerciseId = insertExercise(unitId, 1);
        insertProgress(1);
         markExerciseCompleted();

        String token = loginAndGetToken();

        mockMvc.perform(get("/api/courses/{id}", courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.units[0].exercises[0].completed").value(true))
                .andExpect(jsonPath("$.units[0].exercises[0].locked").value(false));
    }

    @Test
    void getCourseById_locked_exercise() throws Exception {

        UUID exercise1 = insertExercise(unitId, 1);
        UUID exercise2 = insertExercise(unitId, 2);

        insertProgress(1);

        String token = loginAndGetToken();

        mockMvc.perform(get("/api/courses/{id}", courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.units[0].exercises[1].locked").value(true));
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

        insertExercise(unitId, 1);

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

        insertExercise(unitId, 1);
        insertProgress(1);

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
    void startCourse_returns_finish_when_course_completed() throws Exception {

        insertExercise(unitId,1);
        insertProgress(1);
        markUnitCompleted();

        String token = loginAndGetToken();

        mockMvc.perform(post("/api/courses/{id}/start", courseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("FINISH"));
    }
}