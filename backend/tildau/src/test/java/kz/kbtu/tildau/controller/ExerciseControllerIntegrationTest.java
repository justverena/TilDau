package kz.kbtu.tildau.controller;

import kz.kbtu.tildau.TildauApplication;
import kz.kbtu.tildau.service.MinioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {TildauApplication.class, AiSuccessTestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExerciseControllerIntegrationTest extends BaseIntegrationTest {


    @Autowired
    private MinioService minioService;

    @Autowired
    private MockMvc mockMvc;

    protected UUID exerciseId;
    protected UUID otherExerciseId;

    @BeforeEach
    void insertExercises() {

        exerciseId = insertExercise(unitId);
        otherExerciseId = insertExercise(otherUnitId);

        insertProgress();
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

    @Test
    void submitExercise_ReadAloud_Success() throws Exception {
        String token = loginAndGetToken();

        byte[] audioBytes = "audio content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "audio.wav", "audio/wav", audioBytes);

        mockMvc.perform(multipart("/api/exercises/{id}/submit", exerciseId)
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallScore").exists())
                .andExpect(jsonPath("$.feedback").isArray())
                .andExpect(jsonPath("$.feedback[0]").isString());
    }

    @Test
    void submitExercise_RepeatAfterAudio_Success() throws Exception {
        String token = loginAndGetToken();

        String objectName = "exercise/audio.wav";
        minioService.putObject(objectName, "reference audio".getBytes());

        UUID exerciseId = insertRepeatAfterAudioExercise(unitId, objectName);

        byte[] audioBytes = "audio content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "audio.wav", "audio/wav", audioBytes);

        mockMvc.perform(multipart("/api/exercises/{id}/submit", exerciseId)
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallScore").exists())
                .andExpect(jsonPath("$.feedback").isArray())
                .andExpect(jsonPath("$.feedback[0]").isString());
    }

}