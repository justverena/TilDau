package kz.kbtu.tildau.controller;

import kz.kbtu.tildau.service.MinioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ExerciseControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MinioService minioService;

    protected UUID exerciseId;
    protected UUID otherExerciseId;

    @BeforeEach
    void insertExercises() {

        exerciseId = insertExercise(unitId);
        otherExerciseId = insertExercise(otherUnitId);
    }

    @Test
    void getExercise_ReadAloud_success() throws Exception {

        String token = loginAndGetToken();

        mockMvc.perform(get("/api/exercises/{id}", exerciseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseType").value("READ_ALOUD"))
                .andExpect(jsonPath("$.expectedText").value("text"));
    }

    @Test
    void getExercise_RepeatAfterAudio_success() throws Exception {
        String objectName = "exercise/audio.wav";
        minioService.putObject(objectName, "test audio".getBytes());

        UUID exerciseId = insertRepeatAfterAudioExercise(unitId, objectName);

        String token = loginAndGetToken();

        mockMvc.perform(get("/api/exercises/{id}", exerciseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseType").value("REPEAT_AFTER_AUDIO"))
                .andExpect(jsonPath("$.expectedText").doesNotExist())
                .andExpect(jsonPath("$.referenceAudioUrl").exists());
    }

    @Test
    void getExercise_fails_when_not_authenticated() throws Exception {

        mockMvc.perform(get("/api/exercises/{id}", exerciseId))
                .andExpect(status().isForbidden());
    }

    @Test
    void getExercise_fails_when_exercise_not_belongs_to_user_defect() throws Exception {

        String token = loginAndGetToken();

        mockMvc.perform(get("/api/exercises/{id}", otherExerciseId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}