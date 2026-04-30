package kz.kbtu.tildau.service;
import kz.kbtu.tildau.entity.AiAnalysisResult;
import kz.kbtu.tildau.entity.User;
import kz.kbtu.tildau.entity.UserExercise;
import kz.kbtu.tildau.enums.ExerciseStatus;
import kz.kbtu.tildau.repository.AiAnalysisResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {

    @Mock
    private AiAnalysisResultRepository repository;

    @InjectMocks
    private AnalysisService analysisService;

    private final UUID userId = UUID.randomUUID();

    @Test
    void getSuccessfulResults_filtersOnlyCompleted() {
        User user = User.builder().id(userId).build();

        UserExercise completed = UserExercise.builder()
                .user(user)
                .status(ExerciseStatus.COMPLETED)
                .build();

        UserExercise failed = UserExercise.builder()
                .user(user)
                .status(ExerciseStatus.FAILED)
                .build();

        AiAnalysisResult result1 = AiAnalysisResult.builder()
                .userExercise(completed)
                .build();

        AiAnalysisResult result2 = AiAnalysisResult.builder()
                .userExercise(failed)
                .build();

        when(repository.findAll()).thenReturn(List.of(result1, result2));

        List<AiAnalysisResult> result = analysisService.getSuccessfulResults(userId);

        assertEquals(1, result.size());
    }
}