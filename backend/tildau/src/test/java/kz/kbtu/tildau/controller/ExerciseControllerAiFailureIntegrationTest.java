package kz.kbtu.tildau.controller;

import kz.kbtu.tildau.dto.ai.AnalyzeResponse;
import kz.kbtu.tildau.service.AiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExerciseControllerAiFailureIntegrationTest extends BaseIntegrationTest {

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

    @TestConfiguration
    static class AiFailureConfig {

        @Bean
        @Primary
        public AiService aiService(RestTemplate restTemplate) {
            return new AiService(restTemplate) {
                @Override
                public AnalyzeResponse analyze(byte[] audio, String expectedText) {
                    throw new RuntimeException("AI error");
                }
            };
        }
    }

    @Test
    void submitExercise_AiThrowsException_SetsFailed() throws Exception {
        String token = loginAndGetToken();

        MockMultipartFile file =
                new MockMultipartFile("file", "audio.wav", "audio/wav", "audio".getBytes());

        mockMvc.perform(multipart("/api/exercises/{id}/submit", exerciseId)
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isInternalServerError());
    }
}