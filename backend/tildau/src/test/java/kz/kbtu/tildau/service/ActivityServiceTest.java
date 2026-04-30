package kz.kbtu.tildau.service;

import kz.kbtu.tildau.entity.User;
import kz.kbtu.tildau.entity.UserExercise;
import kz.kbtu.tildau.enums.ExerciseStatus;
import kz.kbtu.tildau.repository.UserExerciseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private UserExerciseRepository userExerciseRepository;

    @InjectMocks
    private ActivityService activityService;

    private final UUID userId = UUID.randomUUID();

    @Test
    void getCompletedAttempts_filtersOnlyCompleted() {
        User user = User.builder().id(userId).build();

        UserExercise completed = UserExercise.builder()
                .user(user)
                .status(ExerciseStatus.COMPLETED)
                .build();
        UserExercise failed = UserExercise.builder()
                .user(user)
                .status(ExerciseStatus.FAILED)
                .build();

        when(userExerciseRepository.findAll()).thenReturn(List.of(completed, failed));
        List<UserExercise> result = activityService.getCompletedAttempts(userId);

        assertEquals(1, result.size());
        assertEquals(ExerciseStatus.COMPLETED, result.get(0).getStatus());
    }

    @Test
    void calculateStreak_countsCorrectly() {
        LocalDate today = LocalDate.now();
        Set<LocalDate> days = Set.of(
                today,
                today.minusDays(1),
                today.minusDays(2)
        );

        int streak = activityService.calculateStreak(days);

        assertEquals(3, streak);
    }
}