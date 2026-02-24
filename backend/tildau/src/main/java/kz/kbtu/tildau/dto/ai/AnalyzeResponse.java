package kz.kbtu.tildau.dto.ai;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AnalyzeResponse {

    private int pronunciationScore;

    private int fluencyScore;

    private int embeddingScore;

    private int overallScore;

    private List<String> feedback;

    private List<String> flags;

    private Map<String, Object> metrics;

}
