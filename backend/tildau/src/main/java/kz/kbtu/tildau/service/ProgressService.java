package kz.kbtu.tildau.service;

import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.repository.UserCourseProgressRepository;
import kz.kbtu.tildau.repository.UserUnitProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
@Service
public class ProgressService {

    private final UserUnitProgressRepository userUnitProgressRepository;
    private final UserCourseProgressRepository userCourseProgressRepository;

    public void validateExerciseAccess(User user, Exercise exercise) {

        Unit unit = exercise.getUnit();

        userCourseProgressRepository
                .findByUserIdAndCourseId(user.getId(), unit.getCourse().getId())
                .orElseThrow(() ->
                        new RuntimeException("User has no access to this course"));

        UserUnitProgress unitProgress =
                userUnitProgressRepository
                        .findByUserIdAndUnitId(user.getId(), unit.getId())
                        .orElseThrow(() ->
                                new RuntimeException("User has no access to this unit"));

        if (unitProgress.isCompleted()) {
            throw new RuntimeException("Unit already completed");
        }

        if (exercise.getOrderIndex() >
                unitProgress.getCompletedExercises() + 1) {
            throw new RuntimeException("Exercise is locked");
        }
    }

    @Transactional
    public void handleSuccessfulAttempt(User user, Exercise exercise) {

        Unit unit = exercise.getUnit();
        Course course = unit.getCourse();

        UserUnitProgress unitProgress =
                userUnitProgressRepository
                        .findByUserIdAndUnitId(user.getId(), unit.getId())
                        .orElseThrow();

        if (exercise.getOrderIndex() !=
                unitProgress.getCompletedExercises() + 1) {
            return;
        }

        unitProgress.setCompletedExercises(
                unitProgress.getCompletedExercises() + 1
        );

        if (unitProgress.getCompletedExercises() >=
                unitProgress.getTotalExercises()) {

            unitProgress.setCompleted(true);
            userUnitProgressRepository.save(unitProgress);

            updateCourseProgress(user, course);

        } else {
            userUnitProgressRepository.save(unitProgress);
        }
    }

    private void updateCourseProgress(User user, Course course) {

        UserCourseProgress courseProgress =
                userCourseProgressRepository
                        .findByUserIdAndCourseId(user.getId(), course.getId())
                        .orElseThrow();

        courseProgress.setCompletedUnits(
                courseProgress.getCompletedUnits() + 1
        );

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
}