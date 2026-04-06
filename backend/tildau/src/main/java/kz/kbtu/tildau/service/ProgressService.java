package kz.kbtu.tildau.service;

import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.exception.ForbiddenException;
import kz.kbtu.tildau.repository.UserCourseProgressRepository;
import kz.kbtu.tildau.repository.UserUnitProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProgressService {

    private final UserUnitProgressRepository userUnitProgressRepository;
    private final UserCourseProgressRepository userCourseProgressRepository;

    public void validateExerciseAccess(User user, Exercise exercise) {
        Unit unit = exercise.getUnit();
        Optional<UserCourseProgress> courseProgressOpt = userCourseProgressRepository.findByUserIdAndCourseId(user.getId(), unit.getCourse().getId());

        if (courseProgressOpt.isEmpty()) {
            throw new ForbiddenException("Course not started");
        }

        Optional<UserUnitProgress> unitProgressOpt = userUnitProgressRepository.findByUserIdAndUnitId(user.getId(), unit.getId());

        if (unitProgressOpt.isEmpty()) {
            throw new ForbiddenException("Unit not available");
        }

        UserUnitProgress unitProgress = unitProgressOpt.get();

        if (unitProgress.isCompleted()) {
            throw new ForbiddenException("Unit already completed");
        }

        if (exercise.getOrderIndex() > unitProgress.getCompletedExercises() + 1) {
            throw new ForbiddenException("Exercise is locked");
        }
    }

    @Transactional
    public void handleSuccessfulAttempt(User user, Exercise exercise) {
        Unit unit = exercise.getUnit();
        Course course = unit.getCourse();
        UserUnitProgress unitProgress = getUnitProgressOrThrow(user.getId(), unit.getId());

        if (exercise.getOrderIndex() != unitProgress.getCompletedExercises() + 1) {
            return;
        }

        unitProgress.setCompletedExercises(unitProgress.getCompletedExercises() + 1);

        if (unitProgress.getCompletedExercises() >= unitProgress.getTotalExercises()) {
            unitProgress.setCompleted(true);
            userUnitProgressRepository.save(unitProgress);
            updateCourseProgress(user, course);
        } else {
            userUnitProgressRepository.save(unitProgress);
        }
    }

    private void updateCourseProgress(User user, Course course) {
        UserCourseProgress courseProgress = getCourseProgressOrThrow(user.getId(), course.getId());
        courseProgress.setCompletedUnits(courseProgress.getCompletedUnits() + 1);

        BigDecimal percent =
                BigDecimal.valueOf(courseProgress.getCompletedUnits())
                        .divide(
                                BigDecimal.valueOf(courseProgress.getTotalUnits()),
                                2,
                                RoundingMode.HALF_UP
                        )
                        .multiply(BigDecimal.valueOf(100));

        courseProgress.setProgressPercent(percent);
        userCourseProgressRepository.save(courseProgress);
    }

    private UserCourseProgress getCourseProgressOrThrow(UUID userId, UUID courseId) {
        return userCourseProgressRepository
                .findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() ->
                        new RuntimeException("User has no access to this course"));
    }

    private UserUnitProgress getUnitProgressOrThrow(UUID userId, UUID unitId) {
        return userUnitProgressRepository
                .findByUserIdAndUnitId(userId, unitId)
                .orElseThrow(() ->
                        new RuntimeException("User has no access to this unit"));
    }
}