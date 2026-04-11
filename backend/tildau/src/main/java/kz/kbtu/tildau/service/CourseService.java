package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.course.*;
import kz.kbtu.tildau.dto.nextStep.NextStepResponse;
import kz.kbtu.tildau.embeddedId.UserCourseProgressId;
import kz.kbtu.tildau.embeddedId.UserUnitProgressId;
import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.exception.ForbiddenException;
import kz.kbtu.tildau.exception.NotFoundException;
import kz.kbtu.tildau.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CourseService {

    private final UserJpaRepository userJpaRepository;
    private final UserDefectTypeRepository userDefectTypeRepository;
    private final CourseRepository courseRepository;
    private final UnitRepository unitRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserCourseProgressRepository userCourseProgressRepository;
    private final UserUnitProgressRepository userUnitProgressRepository;
    private final NextStepService nextStepService;

    public List<CourseShortResponse> getCoursesForUser(UUID userId) {
        UserDefectType userDefectType = getUserDefectTypeOrThrow(userId);

        return courseRepository.findByDefectTypeId(userDefectType.getDefectType().getId())
                .stream()
                .map(course -> new CourseShortResponse(
                        course.getId(),
                        course.getTitle()
                ))
                .collect(Collectors.toList());
    }

    public CourseFullResponse getCourseForUser(UUID userId, UUID courseId) {

        UserDefectType userDefectType = getUserDefectTypeOrThrow(userId);
        Course course =  getCourseOrThrow(courseId, userDefectType);
        UserCourseProgress progress = userCourseProgressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElse(null);

        BigDecimal percent = progress != null
                ? progress.getProgressPercent()
                : BigDecimal.ZERO;

        List<UnitResponse> units = getUnitsWithProgress(userId, courseId);

        return new CourseFullResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                percent,
                units
        );
    }

   @Transactional
    public NextStepResponse startCourse(UUID userId, UUID courseId) {
        User user = getUserOrThrow(userId);
        UserDefectType userDefectType = getUserDefectTypeOrThrow(userId);
        Course course =  getCourseOrThrow(courseId, userDefectType);

        List<Unit> units = getUnits(courseId);
        Optional<UserCourseProgress> progress = userCourseProgressRepository.findByUserIdAndCourseId(userId, courseId);

        if (progress.isEmpty()) {
            createCourseProgress(user, course, units);
            createUnitProgress(user, units);
        }
        return nextStepService.getNextStep(userId, courseId);
    }

    private void createUnitProgress(User user, List<Unit> units) {
        for (Unit unit : units) {
            List<Exercise> exercises = exerciseRepository.findByUnitIdOrderByOrderIndex(unit.getId());
            UserUnitProgress unitProgress = new UserUnitProgress();
            unitProgress.setId(
                    UserUnitProgressId.builder()
                            .userId(user.getId())
                            .unitId(unit.getId())
                            .build()
            );
            unitProgress.setUser(user);
            unitProgress.setUnit(unit);
            unitProgress.setCompletedExercises(0);
            unitProgress.setTotalExercises(exercises.size());
            unitProgress.setCompleted(false);
            userUnitProgressRepository.save(unitProgress);
        }
    }

    private List<UnitResponse> getUnitsWithProgress(UUID userId, UUID courseId) {
        List<Unit> units = unitRepository.findByCourseIdOrderByOrderIndex(courseId);

        List<UUID> unitIds = units.stream()
                .map(Unit::getId)
                .toList();

        Map<UUID, UserUnitProgress> progressMap =
                userUnitProgressRepository
                        .findByUserIdAndUnitIdIn(userId, unitIds)
                        .stream()
                        .collect(Collectors.toMap(
                                p -> p.getUnit().getId(),
                                Function.identity()
                        ));

        Map<UUID, List<Exercise>> exercisesMap =
                exerciseRepository
                        .findByUnitIdInOrderByOrderIndex(unitIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                ex -> ex.getUnit().getId()
                        ));

        return units.stream()
                .map(unit -> {
                    UserUnitProgress progress = progressMap.get(unit.getId());
                    List<Exercise> exercises = exercisesMap.getOrDefault(unit.getId(), List.of());
                    List<ExerciseResponse> exerciseResponses = buildExerciseResponses(exercises, progress);
                    return new UnitResponse(
                            unit.getId(),
                            unit.getTitle(),
                            unit.getDescription(),
                            progress != null && progress.isCompleted(),
                            exerciseResponses
                    );
                })
                .toList();
    }

    private List<ExerciseResponse> buildExerciseResponses(List<Exercise> exercises, UserUnitProgress progress) {
        int completed = progress != null ? progress.getCompletedExercises() : 0;

        return exercises.stream()
                .map(ex -> {
                    boolean isCompleted = ex.getOrderIndex() <= completed;
                    boolean isLocked = ex.getOrderIndex() > completed + 1;
                    return new ExerciseResponse(
                            ex.getId(),
                            ex.getTitle(),
                            ex.getInstruction(),
                            isCompleted,
                            isLocked
                    );
                })
                .toList();
    }

    private void createCourseProgress(User user, Course course, List<Unit> units) {
        UserCourseProgress progress = new UserCourseProgress();
        progress.setId(
                UserCourseProgressId.builder()
                        .userId(user.getId())
                        .courseId(course.getId())
                        .build()
        );
        progress.setUser(user);
        progress.setCourse(course);
        progress.setCompletedUnits(0);
        progress.setTotalUnits(units.size());
        progress.setProgressPercent(BigDecimal.ZERO);
        userCourseProgressRepository.save(progress);
    }

    private List<Unit> getUnits(UUID courseId) {
        return unitRepository.findByCourseIdOrderByOrderIndex(courseId);
    }

    private Course getCourseOrThrow(UUID courseId, UserDefectType userDefectType) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));

        if (!course.getDefectType().getId()
                .equals(userDefectType.getDefectType().getId())) {
            throw new ForbiddenException("Course does not belong to user's defect type");
        }
        return course;
    }

    private UserDefectType getUserDefectTypeOrThrow(UUID userId) {
        return userDefectTypeRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User defect type not found"));
    }

    private User getUserOrThrow(UUID userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}