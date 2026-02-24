package kz.kbtu.tildau.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AnalyzeResponse {

    @JsonProperty("pronunciation_score")
    private int pronunciationScore;

    @JsonProperty("fluency_score")
    private int fluencyScore;

    @JsonProperty("embedding_score")
    private int embeddingScore;

    @JsonProperty("overall_score")
    private int overallScore;

    private List<String> feedback;

    private List<String> flags;

    private Map<String, Object> metrics;

}
