package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.stats.AchievementResponse;
import kz.kbtu.tildau.dto.stats.ActivityDayResponse;
import kz.kbtu.tildau.dto.stats.SkillTrendResponse;
import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.enums.ExerciseStatus;
import kz.kbtu.tildau.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final ActivityService activityService;
    private final AnalysisService analysisService;

    public Integer getUserCurrentStreak(UUID userId) {
        List<UserExercise> attempts = activityService.getCompletedAttempts(userId);
        Set<LocalDate> days = activityService.extractActiveDays(attempts);
        return activityService.calculateStreak(days);
    }

    public List<ActivityDayResponse> getUserActivityCalendar(UUID userId) {
        List<UserExercise> attempts = activityService.getCompletedAttempts(userId);

        Map<LocalDate, Long> grouped = attempts.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCompletedAt().toLocalDate(),
                        Collectors.counting()
                ));

        return grouped.entrySet().stream()
                .map(e -> new ActivityDayResponse(e.getKey(), e.getValue().intValue()))
                .sorted(Comparator.comparing(ActivityDayResponse::getDate))
                .toList();
    }

    public SkillTrendResponse getUserSkillTrend(UUID userId) {
        List<AiAnalysisResult> results = analysisService.getSuccessfulResults(userId);
        if (results.isEmpty()) {
            return buildSkillTrendDto(0,0,0);
        }

        double pronunciation = results.stream()
                .mapToDouble(AiAnalysisResult::getPronunciationScore)
                .average().orElse(0);
        double fluency = results.stream()
                .mapToDouble(AiAnalysisResult::getFluencyScore)
                .average().orElse(0);
        double overall = results.stream()
                .mapToDouble(AiAnalysisResult::getOverallScore)
                .average().orElse(0);

        return buildSkillTrendDto(pronunciation,fluency,overall);
    }

    private SkillTrendResponse buildSkillTrendDto(double pronunciation, double fluency, double overall) {
    return SkillTrendResponse.builder()
            .pronunciation(pronunciation)
            .fluency(fluency)
            .overall(overall)
            .build();
    }

}