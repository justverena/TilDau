package kz.kbtu.tildau.controller;

import kz.kbtu.tildau.TildauApplication;
import kz.kbtu.tildau.service.AchievementService;
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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {TildauApplication.class, AiSuccessTestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExerciseControllerIntegrationSuccessTest extends BaseIntegrationTest {


    @Autowired
    private MinioService minioService;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private MockMvc mockMvc;

    protected UUID exerciseId;
    protected UUID exercise2Id;
    protected UUID otherExerciseId;

    @BeforeEach
    void insertExercises() {

        exerciseId = insertExercise(unitId,1 );
        exercise2Id = insertExercise(unitId,2 );
        otherExerciseId = insertExercise(otherUnitId,1);

        insertProgress(2);
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
                .andExpect(status().isNotFound());
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

    @Test
    void submitExercise_Pass_ReturnsNextStepExercise() throws Exception {

        String token = loginAndGetToken();

        MockMultipartFile file = new MockMultipartFile("file", "audio.wav", "audio/wav", "audio".getBytes());

        mockMvc.perform(multipart("/api/exercises/{id}/submit", exerciseId)
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nextStep.type").value("EXERCISE"))
                .andExpect(jsonPath("$.nextStep.id").exists());
    }

    @Test
    void submitExercise_UnlocksAchievement() throws Exception {

        String token = loginAndGetToken();

        MockMultipartFile file =
                new MockMultipartFile("file", "audio.wav", "audio/wav", "audio".getBytes());

        mockMvc.perform(multipart("/api/exercises/{id}/submit", exerciseId)
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newAchievements").isArray())
                .andExpect(jsonPath("$.newAchievements.length()").value(greaterThan(0)))
                .andExpect(jsonPath("$.newAchievements[0].code").exists())
                .andExpect(jsonPath("$.newAchievements[*].code")
                        .value(hasItem("GOOD_SCORE_90")));

    }

    @Test
    void submitExercise_Pass_UpdatesProgress() throws Exception {

        String token = loginAndGetToken();

        MockMultipartFile file =
                new MockMultipartFile("file", "audio.wav", "audio/wav", "audio".getBytes());

        mockMvc.perform(multipart("/api/exercises/{id}/submit", exerciseId)
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        Integer completed = completedExercisesCount(userId, unitId);

        assertEquals(1, completed);
    }

    @Test
    void submitExercise_LastExercise_ReturnsFinish() throws Exception {

        markExerciseCompleted();
        String token = loginAndGetToken();

        MockMultipartFile file =
                new MockMultipartFile("file", "audio.wav", "audio/wav", "audio".getBytes());

        mockMvc.perform(multipart("/api/exercises/{id}/submit", exercise2Id)
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nextStep.type").value("FINISH"))
                .andExpect(jsonPath("$.nextStep.id").doesNotExist());
    }
}