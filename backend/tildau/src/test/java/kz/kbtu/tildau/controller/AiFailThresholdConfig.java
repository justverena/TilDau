package kz.kbtu.tildau.controller;

import kz.kbtu.tildau.dto.ai.AnalyzeResponse;
import kz.kbtu.tildau.service.AiService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@TestConfiguration
public class AiFailThresholdConfig {

    @Bean
    @Primary
    public AiService aiService() {
        return new AiService(new RestTemplate()) {
            @Override
            public AnalyzeResponse analyze(byte[] audioBytes, String expectedText) {
                AnalyzeResponse response = new AnalyzeResponse();
                response.setOverallScore(50);
                response.setFeedback(List.of("Try again"));
                return response;
            }
        };
    }
}