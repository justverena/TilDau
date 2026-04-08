package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.ai.AnalyzeResponse;
import kz.kbtu.tildau.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AiService aiService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aiService, "aiModuleUrl", "http://localhost:8080/analyze");
    }

    @Test
    void analyze_Success() {
        byte[] audioBytes = "audio".getBytes();
        String expectedText = "Expected text";

        AnalyzeResponse mockResponse = new AnalyzeResponse();
        mockResponse.setPronunciationScore(90);
        mockResponse.setFluencyScore(85);
        mockResponse.setEmbeddingScore(88);
        mockResponse.setOverallScore(87);
        mockResponse.setFeedback(List.of("Good job"));
        mockResponse.setFlags(List.of("LOW_FLUENCY"));
        mockResponse.setMetrics(Map.of("silence_ratio", 0.2));

        ResponseEntity<AnalyzeResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(AnalyzeResponse.class))).thenReturn(responseEntity);

        AnalyzeResponse result = aiService.analyze(audioBytes, expectedText);

        assertEquals(87, result.getOverallScore());
        assertEquals(List.of("Good job"), result.getFeedback());
        assertEquals(90, result.getPronunciationScore());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(AnalyzeResponse.class));
    }

    @Test
    void analyze_RestTemplateThrowsException() {
        byte[] audioBytes = "audio".getBytes();
        String expectedText = "Expected text";

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(AnalyzeResponse.class))).thenThrow(new RuntimeException("Connection failed"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> aiService.analyze(audioBytes, expectedText));

        assertTrue(ex.getMessage().contains("Failed to analyze AI module"));
        assertTrue(ex.getCause().getMessage().contains("Connection failed"));
    }
}