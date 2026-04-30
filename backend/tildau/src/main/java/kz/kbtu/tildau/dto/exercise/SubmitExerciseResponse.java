package kz.kbtu.tildau.dto.exercise;

import kz.kbtu.tildau.dto.nextStep.NextStepResponse;
import kz.kbtu.tildau.dto.stats.AchievementResponse;
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
    private NextStepResponse nextStep;
    private List<AchievementResponse> newAchievements;
}
