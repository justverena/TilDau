package kz.kbtu.tildau.controller;

import kz.kbtu.tildau.dto.ai.AnalyzeResponse;
import kz.kbtu.tildau.service.AiService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@TestConfiguration
public class AiSuccessTestConfig {

    @Bean
    @Primary
    public AiService aiService() {
        return new AiService(new RestTemplate()) {
            @Override
            public AnalyzeResponse analyze(byte[] audioBytes, String expectedText) {
                if (expectedText == null) expectedText = "";
                AnalyzeResponse response = new AnalyzeResponse();
                response.setPronunciationScore(90);
                response.setFluencyScore(90);
                response.setEmbeddingScore(90);
                response.setOverallScore(95);
                response.setFeedback(List.of("Good pronunciation","Stable fluency"));
                response.setFlags(List.of());
                response.setMetrics(Map.of());
                return response;
            }
        };
    }
}