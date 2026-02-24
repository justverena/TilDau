package kz.kbtu.tildau.dto.exercise;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SubmitExerciseResponse {
    private UUID attemptId;
    private int overallScore;
    private List<String> feedback;
}
