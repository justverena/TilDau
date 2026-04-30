package kz.kbtu.tildau.service;
import kz.kbtu.tildau.dto.stats.AchievementResponse;
import kz.kbtu.tildau.embeddedId.UserAchievementId;
import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.exception.NotFoundException;
import kz.kbtu.tildau.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final ActivityService activityService;
    private final AnalysisService analysisService;

    public List<AchievementResponse> getUserAchievements(UUID userId) {
        return userAchievementRepository.findAllByUserId(userId)
                .stream()
                .map(this::buildAchievementResponse)
                .toList();
    }

    public List<AchievementResponse> checkAchievements(UUID userId) {
        List<UserAchievement> unlocked = new ArrayList<>();
        unlocked.addAll(checkGoodScoreAchievements(userId));
        unlocked.addAll(checkStreakAchievements(userId));
        unlocked.addAll(checkUnitAchievements(userId));
        unlocked.addAll(checkCourseAchievements(userId));

        return unlocked.stream()
                .map(this::buildAchievementResponse)
                .toList();
    }

    private List<UserAchievement> checkGoodScoreAchievements(UUID userId) {
        List<UserAchievement> unlocked = new ArrayList<>();
        List<AiAnalysisResult> results = analysisService.getSuccessfulResults(userId);
        boolean has90 = results.stream()
                .anyMatch(r -> r.getOverallScore() >= 90);

        if (has90) {
            unlock(userId, "GOOD_SCORE_90").ifPresent(unlocked::add);
        }
        unlocked.addAll(checkConsecutiveScores(userId, results));
        return unlocked;
    }

    private List<UserAchievement> checkConsecutiveScores(UUID userId, List<AiAnalysisResult> results) {
        List<UserAchievement> unlocked = new ArrayList<>();
        List<AiAnalysisResult> sorted = results.stream()
                .sorted(Comparator.comparing(AiAnalysisResult::getCreatedAt).reversed())
                .toList();
        int streak = 0;
        for (AiAnalysisResult r : sorted) {
            if (r.getOverallScore() >= 90) {
                streak++;
            } else {
                break;
            }
        }
        if (streak >= 3) {
            unlock(userId, "GOOD_SCORE_90_3").ifPresent(unlocked::add);
        }
        if (streak >= 5) {
            unlock(userId, "GOOD_SCORE_90_5").ifPresent(unlocked::add);
        }
        return unlocked;
    }

    private List<UserAchievement> checkStreakAchievements(UUID userId) {
        List<UserAchievement> unlocked = new ArrayList<>();
        List<UserExercise> attempts = activityService.getCompletedAttempts(userId);
        Set<LocalDate> days = activityService.extractActiveDays(attempts);
        int streak = activityService.calculateStreak(days);

        if (streak >= 3) {
            unlock(userId, "STREAK_3").ifPresent(unlocked::add);
        }
        if (streak >= 7) {
            unlock(userId, "STREAK_7").ifPresent(unlocked::add);
        }
        if (streak >= 14) {
            unlock(userId, "STREAK_14").ifPresent(unlocked::add);
        }
        return unlocked;
    }

    private List<UserAchievement> checkUnitAchievements(UUID userId) {
        List<UserAchievement> unlocked = new ArrayList<>();
        List<AiAnalysisResult> results = analysisService.getSuccessfulResults(userId);
        Map<UUID, List<AiAnalysisResult>> byUnit = results.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getUserExercise().getExercise().getUnit().getId()
                ));

        for (List<AiAnalysisResult> unitResults : byUnit.values()) {
            double avg = unitResults.stream()
                    .mapToDouble(AiAnalysisResult::getOverallScore)
                    .average()
                    .orElse(0);

            if (avg >= 90) {
                unlock(userId, "UNIT_90").ifPresent(unlocked::add);
            }
            if (avg >= 95) {
                unlock(userId, "UNIT_95").ifPresent(unlocked::add);
            }
        }
        return unlocked;
    }

    private List<UserAchievement> checkCourseAchievements(UUID userId) {
        List<UserAchievement> unlocked = new ArrayList<>();
        List<AiAnalysisResult> results = analysisService.getSuccessfulResults(userId);
        Map<UUID, List<AiAnalysisResult>> byCourse = results.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getUserExercise().getExercise().getUnit().getCourse().getId()
                ));

        for (List<AiAnalysisResult> courseResults : byCourse.values()) {
            double avg = courseResults.stream()
                    .mapToDouble(AiAnalysisResult::getOverallScore)
                    .average()
                    .orElse(0);

            if (avg >= 90) {
                unlock(userId, "COURSE_90").ifPresent(unlocked::add);
            }
        }
        return unlocked;
    }

    protected Optional<UserAchievement> unlock(UUID userId, String code) {
        boolean exists = userAchievementRepository
                .existsByUserIdAndAchievement_Code(userId, code);
        if (exists) return Optional.empty();
        Achievement achievement = achievementRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Achievement not found"));
        UserAchievement userAchievement = UserAchievement.builder()
                .id(new UserAchievementId(userId, achievement.getId()))
                .user(User.builder().id(userId).build())
                .achievement(achievement)
                .unlockedAt(LocalDateTime.now())
                .build();
        userAchievementRepository.save(userAchievement);
        return Optional.of(userAchievement);
    }

    private AchievementResponse buildAchievementResponse(UserAchievement ua) {
        return AchievementResponse.builder()
                .code(ua.getAchievement().getCode())
                .title(ua.getAchievement().getTitle())
                .description(ua.getAchievement().getDescription())
                .unlockedAt(ua.getUnlockedAt())
                .build();
    }
}