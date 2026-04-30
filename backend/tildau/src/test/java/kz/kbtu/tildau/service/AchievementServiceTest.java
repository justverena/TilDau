package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.stats.AchievementResponse;
import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.enums.ExerciseStatus;
import kz.kbtu.tildau.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AchievementServiceTest {

    @Mock private AchievementRepository achievementRepository;
    @Mock private UserAchievementRepository userAchievementRepository;
    @Mock private ActivityService activityService;
    @Mock private AnalysisService analysisService;

    @InjectMocks
    private AchievementService achievementService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        lenient().when(userAchievementRepository.existsByUserIdAndAchievement_Code(any(), any()))
                .thenReturn(false);

        lenient().when(achievementRepository.findByCode(anyString()))
                .thenAnswer(invocation -> {
                    Achievement a = new Achievement();
                    a.setId(UUID.randomUUID());
                    a.setCode(invocation.getArgument(0));
                    return Optional.of(a);
                });
    }

    @Test
    void unlock_Success() {
        Optional<UserAchievement> result =
                achievementService.unlock(userId, "GOOD_SCORE_90");

        assertTrue(result.isPresent());
        verify(userAchievementRepository).save(any());
    }

    @Test
    void unlock_AlreadyExists() {
        when(userAchievementRepository.existsByUserIdAndAchievement_Code(userId, "GOOD_SCORE_90"))
                .thenReturn(true);

        Optional<UserAchievement> result =
                achievementService.unlock(userId, "GOOD_SCORE_90");

        assertTrue(result.isEmpty());
    }

    @Test
    void unlock_NotFound() {
        when(achievementRepository.findByCode("BAD_CODE"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> achievementService.unlock(userId, "BAD_CODE"));
    }

    @Test
    void checkAchievements_Empty() {
        when(analysisService.getSuccessfulResults(userId)).thenReturn(List.of());
        when(activityService.getCompletedAttempts(userId)).thenReturn(List.of());

        var result = achievementService.checkAchievements(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void goodScore90_Unlocked() {
        when(analysisService.getSuccessfulResults(userId))
                .thenReturn(List.of(mockResult(95)));

        var result = achievementService.checkAchievements(userId);

        assertFalse(result.isEmpty());
    }

    @Test
    void consecutive3_Unlocked() {
        when(analysisService.getSuccessfulResults(userId))
                .thenReturn(List.of(
                        mockResult(95),
                        mockResult(92),
                        mockResult(91)
                ));

        var result = achievementService.checkAchievements(userId);

        assertFalse(result.isEmpty());
    }

    @Test
    void consecutiveBroken_NotUnlocked() {
        when(analysisService.getSuccessfulResults(userId))
                .thenReturn(List.of(
                        mockResult(95),
                        mockResult(92),
                        mockResult(80),
                        mockResult(95)
                ));

        var result = achievementService.checkAchievements(userId);

        assertTrue(result.stream()
                .noneMatch(a -> a.getCode().equals("GOOD_SCORE_90_3")));
    }

    @Test
    void streak3_Unlocked() {
        List<UserExercise> attempts = List.of(
                mockAttempt(LocalDate.now()),
                mockAttempt(LocalDate.now().minusDays(1)),
                mockAttempt(LocalDate.now().minusDays(2))
        );

        when(activityService.getCompletedAttempts(userId)).thenReturn(attempts);
        when(activityService.extractActiveDays(attempts)).thenCallRealMethod();
        when(activityService.calculateStreak(any())).thenReturn(3);

        var result = achievementService.checkAchievements(userId);

        assertFalse(result.isEmpty());
    }

    @Test
    void unit90_Unlocked() {
        UUID unitId = UUID.randomUUID();

        when(analysisService.getSuccessfulResults(userId))
                .thenReturn(List.of(
                        mockResultWithUnit(95, unitId),
                        mockResultWithUnit(92, unitId)
                ));

        var result = achievementService.checkAchievements(userId);

        assertFalse(result.isEmpty());
    }

    @Test
    void course90_Unlocked() {
        UUID courseId = UUID.randomUUID();

        when(analysisService.getSuccessfulResults(userId))
                .thenReturn(List.of(
                        mockResultWithCourse(95, courseId),
                        mockResultWithCourse(92, courseId)
                ));

        var result = achievementService.checkAchievements(userId);

        assertFalse(result.isEmpty());
    }

    private AiAnalysisResult mockResult(int score) {
        AiAnalysisResult r = new AiAnalysisResult();
        r.setOverallScore(score);
        r.setCreatedAt(LocalDateTime.now());

        Course course = new Course();
        course.setId(UUID.randomUUID());

        Unit unit = new Unit();
        unit.setId(UUID.randomUUID());
        unit.setCourse(course);

        Exercise exercise = new Exercise();
        exercise.setUnit(unit);

        UserExercise ue = new UserExercise();
        ue.setStatus(ExerciseStatus.COMPLETED);
        ue.setExercise(exercise);

        r.setUserExercise(ue);
        return r;
    }

    private AiAnalysisResult mockResultWithUnit(int score, UUID unitId) {
        AiAnalysisResult r = mockResult(score);

        Course course = new Course();
        course.setId(UUID.randomUUID());

        Unit unit = new Unit();
        unit.setId(unitId);
        unit.setCourse(course);

        Exercise exercise = new Exercise();
        exercise.setUnit(unit);

        r.getUserExercise().setExercise(exercise);
        return r;
    }

    private AiAnalysisResult mockResultWithCourse(int score, UUID courseId) {
        AiAnalysisResult r = mockResult(score);

        Course course = new Course();
        course.setId(courseId);

        Unit unit = new Unit();
        unit.setId(UUID.randomUUID());
        unit.setCourse(course);

        Exercise exercise = new Exercise();
        exercise.setUnit(unit);

        r.getUserExercise().setExercise(exercise);
        return r;
    }

    private UserExercise mockAttempt(LocalDate date) {
        UserExercise ue = new UserExercise();
        ue.setStatus(ExerciseStatus.COMPLETED);
        ue.setCompletedAt(date.atStartOfDay());
        return ue;
    }
}