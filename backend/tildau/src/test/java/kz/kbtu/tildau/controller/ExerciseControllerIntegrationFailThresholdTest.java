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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {TildauApplication.class, AiFailThresholdConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExerciseControllerIntegrationFailThresholdTest extends BaseIntegrationTest {
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
    void submitExercise_Fail_ReturnsRetry() throws Exception {

        String token = loginAndGetToken();

        MockMultipartFile file = new MockMultipartFile("file", "audio.wav", "audio/wav", "audio".getBytes());

        mockMvc.perform(multipart("/api/exercises/{id}/submit", exerciseId)
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nextStep.type").value("RETRY"))
                .andExpect(jsonPath("$.nextStep.id").value(exerciseId.toString()));
    }

    @Test
    void submitExercise_Fail_DoesNotUpdateProgress() throws Exception {

        String token = loginAndGetToken();

        MockMultipartFile file =
                new MockMultipartFile("file", "audio.wav", "audio/wav", "audio".getBytes());

        mockMvc.perform(multipart("/api/exercises/{id}/submit", exerciseId)
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        Integer completed = completedExercisesCount(userId, unitId);

        assertEquals(0, completed);
    }

}