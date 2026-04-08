package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.nextStep.NextStepResponse;
import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.enums.NextStepType;
import kz.kbtu.tildau.exception.ForbiddenException;
import kz.kbtu.tildau.exception.NotFoundException;
import kz.kbtu.tildau.repository.ExerciseRepository;
import kz.kbtu.tildau.repository.UnitRepository;
import kz.kbtu.tildau.repository.UserUnitProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NextStepServiceTest {

    @Mock
    private UserUnitProgressRepository userUnitProgressRepository;
    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private NextStepService nextStepService;

    private UUID userId;
    private Course course;
    private Unit unit1;
    private Unit unit2;
    private Exercise exercise1;
    private Exercise exercise2;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        course = new Course();
        course.setId(UUID.randomUUID());

        unit1 = new Unit();
        unit1.setId(UUID.randomUUID());
        unit1.setCourse(course);

        unit2 = new Unit();
        unit2.setId(UUID.randomUUID());
        unit2.setCourse(course);

        exercise1 = new Exercise();
        exercise1.setId(UUID.randomUUID());
        exercise1.setUnit(unit1);
        exercise1.setOrderIndex(1);

        exercise2 = new Exercise();
        exercise2.setId(UUID.randomUUID());
        exercise2.setUnit(unit1);
        exercise2.setOrderIndex(2);
    }

    @Test
    void getNextStep_ReturnsFirstExercise() {

        UserUnitProgress progress = new UserUnitProgress();
        progress.setUnit(unit1);
        progress.setCompleted(false);
        progress.setCompletedExercises(0);

        when(unitRepository.findByCourseIdOrderByOrderIndex(course.getId())).thenReturn(List.of(unit1));
        when(userUnitProgressRepository.findByUserIdAndUnitIdIn(userId, List.of(unit1.getId()))).thenReturn(List.of(progress));
        when(exerciseRepository.findByUnitIdInOrderByOrderIndex(List.of(unit1.getId()))).thenReturn(List.of(exercise1, exercise2));

        NextStepResponse result = nextStepService.getNextStep(userId, course.getId());

        assertEquals(NextStepType.EXERCISE, result.getType());
        assertEquals(exercise1.getId(), result.getId());
    }

    @Test
    void getNextStep_ReturnsNextExercise() {

        UserUnitProgress progress = new UserUnitProgress();
        progress.setUnit(unit1);
        progress.setCompleted(false);
        progress.setCompletedExercises(1);

        when(unitRepository.findByCourseIdOrderByOrderIndex(course.getId())).thenReturn(List.of(unit1));
        when(userUnitProgressRepository.findByUserIdAndUnitIdIn(userId, List.of(unit1.getId()))).thenReturn(List.of(progress));
        when(exerciseRepository.findByUnitIdInOrderByOrderIndex(List.of(unit1.getId()))).thenReturn(List.of(exercise1, exercise2));

        NextStepResponse result = nextStepService.getNextStep(userId, course.getId());

        assertEquals(exercise2.getId(), result.getId());
    }

    @Test
    void getNextStep_Finish_WhenCourseCompleted() {

        UserUnitProgress progress = new UserUnitProgress();
        progress.setUnit(unit1);
        progress.setCompleted(true);

        when(unitRepository.findByCourseIdOrderByOrderIndex(course.getId())).thenReturn(List.of(unit1));
        when(userUnitProgressRepository.findByUserIdAndUnitIdIn(userId, List.of(unit1.getId()))).thenReturn(List.of(progress));

        NextStepResponse result = nextStepService.getNextStep(userId, course.getId());

        assertEquals(NextStepType.FINISH, result.getType());
        assertNull(result.getId());
    }

    @Test
    void getNextStep_Fails_WhenNoUnitProgress() {

        when(unitRepository.findByCourseIdOrderByOrderIndex(course.getId())).thenReturn(List.of(unit1));
        when(userUnitProgressRepository.findByUserIdAndUnitIdIn(userId, List.of(unit1.getId()))).thenReturn(List.of());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> nextStepService.getNextStep(userId, course.getId()));

        assertEquals("Unit progress not found", ex.getMessage());
    }

    @Test
    void getNextStepAfterExercise_SameUnit() {

        UserUnitProgress progress = new UserUnitProgress();
        progress.setCompleted(false);
        progress.setCompletedExercises(1);

        when(userUnitProgressRepository.findByUserIdAndUnitId(userId, unit1.getId())).thenReturn(Optional.of(progress));
        when(exerciseRepository.findByUnitIdOrderByOrderIndex(unit1.getId())).thenReturn(List.of(exercise1, exercise2));

        NextStepResponse result = nextStepService.getNextStepAfterExercise(userId, exercise1);

        assertEquals(exercise2.getId(), result.getId());
    }

    @Test
    void getNextStepAfterExercise_NextUnit() {

        UserUnitProgress currentProgress = new UserUnitProgress();
        currentProgress.setCompleted(true);

        UserUnitProgress nextProgress = new UserUnitProgress();
        nextProgress.setCompleted(false);
        nextProgress.setCompletedExercises(0);

        Exercise nextExercise = new Exercise();
        nextExercise.setId(UUID.randomUUID());
        nextExercise.setUnit(unit2);
        nextExercise.setOrderIndex(1);

        when(userUnitProgressRepository.findByUserIdAndUnitId(userId, unit1.getId())).thenReturn(Optional.of(currentProgress));
        when(unitRepository.findByCourseIdOrderByOrderIndex(course.getId())).thenReturn(List.of(unit1, unit2));
        when(userUnitProgressRepository.findByUserIdAndUnitId(userId, unit2.getId())).thenReturn(Optional.of(nextProgress));
        when(exerciseRepository.findByUnitIdOrderByOrderIndex(unit1.getId())).thenReturn(List.of(exercise1, exercise2));
        when(exerciseRepository.findByUnitIdOrderByOrderIndex(unit2.getId())).thenReturn(List.of(nextExercise));

        NextStepResponse result = nextStepService.getNextStepAfterExercise(userId, exercise1);

        assertEquals(nextExercise.getId(), result.getId());
    }

    @Test
    void getNextStepAfterExercise_Finish() {

        UserUnitProgress progress = new UserUnitProgress();
        progress.setCompleted(true);

        when(userUnitProgressRepository.findByUserIdAndUnitId(userId, unit1.getId())).thenReturn(Optional.of(progress));
        when(unitRepository.findByCourseIdOrderByOrderIndex(course.getId())).thenReturn(List.of(unit1));

        NextStepResponse result = nextStepService.getNextStepAfterExercise(userId, exercise1);

        assertEquals(NextStepType.FINISH, result.getType());
    }
}