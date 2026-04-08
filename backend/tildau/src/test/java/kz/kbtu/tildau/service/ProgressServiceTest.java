package kz.kbtu.tildau.service;


import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.exception.ForbiddenException;
import kz.kbtu.tildau.repository.UserCourseProgressRepository;
import kz.kbtu.tildau.repository.UserUnitProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock private UserUnitProgressRepository userUnitProgressRepository;
    @Mock private UserCourseProgressRepository userCourseProgressRepository;
    @InjectMocks private ProgressService progressService;

    private User user;
    private Course course;
    private Unit unit;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());

        course = new Course();
        course.setId(UUID.randomUUID());

        unit = new Unit();
        unit.setId(UUID.randomUUID());
        unit.setCourse(course);

        exercise = new Exercise();
        exercise.setId(UUID.randomUUID());
        exercise.setUnit(unit);
        exercise.setOrderIndex(1);
    }

    @Test
    void validateExerciseAccess_Success() {
        UserCourseProgress courseProgress = new UserCourseProgress();
        courseProgress.setCompletedUnits(0);

        UserUnitProgress unitProgress = new UserUnitProgress();
        unitProgress.setCompleted(false);
        unitProgress.setCompletedExercises(0);
        unitProgress.setTotalExercises(3);

        when(userCourseProgressRepository.findByUserIdAndCourseId(user.getId(), course.getId())).thenReturn(Optional.of(courseProgress));
        when(userUnitProgressRepository.findByUserIdAndUnitId(user.getId(), unit.getId())).thenReturn(Optional.of(unitProgress));

        assertDoesNotThrow(() -> progressService.validateExerciseAccess(user, exercise));
    }

    @Test
    void validateExerciseAccess_Fails_WhenNoCourseAccess() {
        when(userCourseProgressRepository.findByUserIdAndCourseId(user.getId(), course.getId())).thenReturn(Optional.empty());

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> progressService.validateExerciseAccess(user, exercise));

        assertEquals("Course not started", ex.getMessage());
    }

    @Test
    void validateExerciseAccess_Fails_WhenNoUnitAccess() {
        UserCourseProgress courseProgress = new UserCourseProgress();

        when(userCourseProgressRepository.findByUserIdAndCourseId(user.getId(), course.getId())).thenReturn(Optional.of(courseProgress));
        when(userUnitProgressRepository.findByUserIdAndUnitId(user.getId(), unit.getId())).thenReturn(Optional.empty());

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> progressService.validateExerciseAccess(user, exercise));

        assertEquals("Unit not available", ex.getMessage());
    }

    @Test
    void validateExerciseAccess_Fails_WhenUnitCompleted() {
        UserCourseProgress courseProgress = new UserCourseProgress();

        UserUnitProgress unitProgress = new UserUnitProgress();
        unitProgress.setCompleted(true);
        unitProgress.setCompletedExercises(3);
        unitProgress.setTotalExercises(3);

        when(userCourseProgressRepository.findByUserIdAndCourseId(user.getId(), course.getId())).thenReturn(Optional.of(courseProgress));
        when(userUnitProgressRepository.findByUserIdAndUnitId(user.getId(), unit.getId())).thenReturn(Optional.of(unitProgress));

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> progressService.validateExerciseAccess(user, exercise));

        assertEquals("Unit already completed", ex.getMessage());
    }
    @Test
    void validateExerciseAccess_Fails_WhenExerciseLocked() {
        UserCourseProgress courseProgress = new UserCourseProgress();

        UserUnitProgress unitProgress = new UserUnitProgress();
        unitProgress.setCompleted(false);
        unitProgress.setCompletedExercises(0);
        unitProgress.setTotalExercises(3);

        exercise.setOrderIndex(3);

        when(userCourseProgressRepository.findByUserIdAndCourseId(user.getId(), course.getId())).thenReturn(Optional.of(courseProgress));
        when(userUnitProgressRepository.findByUserIdAndUnitId(user.getId(), unit.getId())).thenReturn(Optional.of(unitProgress));

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> progressService.validateExerciseAccess(user, exercise));

        assertEquals("Exercise is locked", ex.getMessage());
    }

    @Test
    void handleSuccessfulAttempt_IncrementsUnitProgress() {
        UserUnitProgress unitProgress = new UserUnitProgress();
        unitProgress.setCompletedExercises(0);
        unitProgress.setTotalExercises(3);
        unitProgress.setCompleted(false);

        when(userUnitProgressRepository.findByUserIdAndUnitId(user.getId(), unit.getId())).thenReturn(Optional.of(unitProgress));

        progressService.handleSuccessfulAttempt(user, exercise);

        assertEquals(1, unitProgress.getCompletedExercises());
        assertFalse(unitProgress.isCompleted());

        verify(userUnitProgressRepository).save(unitProgress);
    }

    @Test
    void handleSuccessfulAttempt_CompletesUnitAndUpdatesCourse() {
        UserUnitProgress unitProgress = new UserUnitProgress();
        unitProgress.setCompletedExercises(0);
        unitProgress.setTotalExercises(1);
        unitProgress.setCompleted(false);

        UserCourseProgress courseProgress = new UserCourseProgress();
        courseProgress.setCompletedUnits(0);
        courseProgress.setTotalUnits(2);

        when(userUnitProgressRepository.findByUserIdAndUnitId(user.getId(), unit.getId())).thenReturn(Optional.of(unitProgress));
        when(userCourseProgressRepository.findByUserIdAndCourseId(user.getId(), course.getId())).thenReturn(Optional.of(courseProgress));

        progressService.handleSuccessfulAttempt(user, exercise);

        assertTrue(unitProgress.isCompleted());
        assertEquals(1, courseProgress.getCompletedUnits());
        assertEquals(new BigDecimal("50.00"), courseProgress.getProgressPercent());
        verify(userUnitProgressRepository).save(unitProgress);
        verify(userCourseProgressRepository).save(courseProgress);
    }

    @Test
    void handleSuccessfulAttempt_ExerciseOutOfOrder_DoesNothing() {
        UserUnitProgress unitProgress = new UserUnitProgress();
        unitProgress.setCompletedExercises(0);
        unitProgress.setTotalExercises(3);

        exercise.setOrderIndex(3);

        when(userUnitProgressRepository.findByUserIdAndUnitId(user.getId(), unit.getId())).thenReturn(Optional.of(unitProgress));

        progressService.handleSuccessfulAttempt(user, exercise);

        assertEquals(0, unitProgress.getCompletedExercises());
        verify(userUnitProgressRepository, never()).save(unitProgress);
    }

    @Test
    void handleSuccessfulAttempt_Fails_WhenUnitProgressNotFound() {
        when(userUnitProgressRepository.findByUserIdAndUnitId(user.getId(), unit.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> progressService.handleSuccessfulAttempt(user, exercise));
    }
}