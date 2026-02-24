package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.ai.AnalyzeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@RequiredArgsConstructor
@Service
public class AiService {

    @Value("${ai.module.url}")
    private String aiModuleUrl;

    private final RestTemplate restTemplate;

    public AnalyzeResponse analyze(byte[] audioBytes, String expectedText) {
        System.out.println("Calling AI module at: " + aiModuleUrl);
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            ByteArrayResource audioResource = new ByteArrayResource(audioBytes){
                @Override
                public String getFilename() {
                    return "user_audio.wav";
                }
            };
            body.add("file", audioResource);
            body.add("expectedText", expectedText);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<AnalyzeResponse> response = restTemplate.postForEntity(
                    aiModuleUrl, request, AnalyzeResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("AI module returned error or empty response");
            }
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze AI module", e);
        }
    }
}
