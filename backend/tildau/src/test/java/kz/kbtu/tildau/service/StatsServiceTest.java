package kz.kbtu.tildau.service;
import kz.kbtu.tildau.dto.stats.SkillTrendResponse;
import kz.kbtu.tildau.dto.stats.ActivityDayResponse;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private ActivityService activityService;

    @Mock
    private AnalysisService analysisService;

    @InjectMocks
    private StatsService statsService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void getUserCurrentStreak_NoActivity_ReturnsZero() {
        when(activityService.getCompletedAttempts(userId)).thenReturn(List.of());
        when(activityService.extractActiveDays(List.of())).thenReturn(Set.of());
        when(activityService.calculateStreak(Set.of())).thenReturn(0);

        Integer streak = statsService.getUserCurrentStreak(userId);

        assertEquals(0, streak);
    }

    @Test
    void getUserCurrentStreak_ValidStreak_ReturnsCorrectValue() {
        List<UserExercise> attempts = List.of(
                mockAttempt(LocalDate.now()),
                mockAttempt(LocalDate.now().minusDays(1)),
                mockAttempt(LocalDate.now().minusDays(2))
        );

        Set<LocalDate> days = Set.of(
                LocalDate.now(),
                LocalDate.now().minusDays(1),
                LocalDate.now().minusDays(2)
        );

        when(activityService.getCompletedAttempts(userId)).thenReturn(attempts);
        when(activityService.extractActiveDays(attempts)).thenReturn(days);
        when(activityService.calculateStreak(days)).thenReturn(3);

        Integer streak = statsService.getUserCurrentStreak(userId);

        assertEquals(3, streak);
    }


    @Test
    void getUserActivityCalendar_NoAttempts_ReturnsEmptyList() {
        when(activityService.getCompletedAttempts(userId)).thenReturn(List.of());

        List<ActivityDayResponse> result = statsService.getUserActivityCalendar(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserActivityCalendar_GroupedCorrectly() {
        List<UserExercise> attempts = List.of(
                mockAttempt(LocalDate.of(2026, 4, 10)),
                mockAttempt(LocalDate.of(2026, 4, 10)),
                mockAttempt(LocalDate.of(2026, 4, 11))
        );

        when(activityService.getCompletedAttempts(userId)).thenReturn(attempts);

        List<ActivityDayResponse> result = statsService.getUserActivityCalendar(userId);

        assertEquals(2, result.size());

        assertTrue(result.stream().anyMatch(r ->
                r.getDate().equals(LocalDate.of(2026, 4, 10)) && r.getExercisesCompleted() == 2
        ));

        assertTrue(result.stream().anyMatch(r ->
                r.getDate().equals(LocalDate.of(2026, 4, 11)) && r.getExercisesCompleted() == 1
        ));
    }

    @Test
    void getUserSkillTrend_NoResults_ReturnsZeroes() {
        when(analysisService.getSuccessfulResults(userId)).thenReturn(List.of());

        SkillTrendResponse response = statsService.getUserSkillTrend(userId);

        assertEquals(0, response.getOverall());
        assertEquals(0, response.getFluency());
        assertEquals(0, response.getPronunciation());
    }

    @Test
    void getUserSkillTrend_ReturnsAverages() {
        List<AiAnalysisResult> results = List.of(
                mockResult(80, 70, 75),
                mockResult(100, 90, 95)
        );

        when(analysisService.getSuccessfulResults(userId)).thenReturn(results);

        SkillTrendResponse response = statsService.getUserSkillTrend(userId);

        assertEquals(90, response.getOverall(), 0.01);
        assertEquals(80, response.getFluency(), 0.01);
        assertEquals(85, response.getPronunciation(), 0.01);
    }

    private UserExercise mockAttempt(LocalDate date) {
        UserExercise ue = new UserExercise();
        ue.setStatus(ExerciseStatus.COMPLETED);
        ue.setCompletedAt(date.atStartOfDay());
        return ue;
    }

    private AiAnalysisResult mockResult(int overall, int fluency, int pronunciation) {
        AiAnalysisResult result = new AiAnalysisResult();
        result.setOverallScore(overall);
        result.setFluencyScore(fluency);
        result.setPronunciationScore(pronunciation);

        UserExercise ue = new UserExercise();
        ue.setStatus(ExerciseStatus.COMPLETED);
        result.setUserExercise(ue);

        return result;
    }
}