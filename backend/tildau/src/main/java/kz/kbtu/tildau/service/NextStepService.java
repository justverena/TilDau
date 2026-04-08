package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.nextStep.NextStepResponse;
import kz.kbtu.tildau.entity.Exercise;
import kz.kbtu.tildau.entity.Unit;
import kz.kbtu.tildau.entity.UserUnitProgress;
import kz.kbtu.tildau.enums.NextStepType;
import kz.kbtu.tildau.exception.ForbiddenException;
import kz.kbtu.tildau.exception.NotFoundException;
import kz.kbtu.tildau.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class NextStepService {
    private final UserUnitProgressRepository userUnitProgressRepository;
    private final ExerciseRepository exerciseRepository;
    private final UnitRepository unitRepository;

    public NextStepResponse getNextStep(UUID userId, UUID courseId) {
        List<Unit> units = unitRepository.findByCourseIdOrderByOrderIndex(courseId);
        UnitWithProgress next = findNextUnit(userId, units);

        if (next == null) {
            return buildNextStepResponse(NextStepType.FINISH, null);
        }

        Map<UUID, List<Exercise>> exercisesMap = getExercisesMap(units);
        List<Exercise> exercises = exercisesMap.getOrDefault(next.unit().getId(), List.of());
        UUID nextExerciseId = findNextExerciseInUnit(next.progress(), exercises);
        return buildNextStepResponse(NextStepType.EXERCISE, nextExerciseId);
    }



    public NextStepResponse getNextStepAfterExercise(UUID userId, Exercise exercise) {
        Unit currentUnit = exercise.getUnit();
        UserUnitProgress progress = getUnitProgressOrThrow(userId, currentUnit.getId());
        List<Exercise> currentExercises = exerciseRepository.findByUnitIdOrderByOrderIndex(currentUnit.getId());
        if (!progress.isCompleted()) {
            UUID nextExerciseId = findNextExerciseInUnit(progress, currentExercises);
            return buildNextStepResponse(NextStepType.EXERCISE, nextExerciseId);
        }
        List<Unit> units = unitRepository.findByCourseIdOrderByOrderIndex(currentUnit.getCourse().getId());
        Unit nextUnit = findNextUnitAfter(currentUnit, units);

        if (nextUnit == null) {
            return buildNextStepResponse(NextStepType.FINISH, null);
        }

        UserUnitProgress nextUnitProgress = getUnitProgressOrThrow(userId, nextUnit.getId());
        List<Exercise> nextExercises = exerciseRepository.findByUnitIdOrderByOrderIndex(nextUnit.getId());
        UUID nextExerciseId = findNextExerciseInUnit(nextUnitProgress, nextExercises);

        return buildNextStepResponse(NextStepType.EXERCISE, nextExerciseId);
    }

    private Unit findNextUnitAfter(Unit currentUnit, List<Unit> units) {

        int index = IntStream.range(0, units.size())
                .filter(i -> units.get(i).getId().equals(currentUnit.getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Unit not found"));

        return (index + 1 < units.size()) ? units.get(index + 1) : null;
    }

    public record UnitWithProgress(Unit unit, UserUnitProgress progress) {}

    private UnitWithProgress findNextUnit(UUID userId, List<Unit> units) {

        Map<UUID, UserUnitProgress> progressMap = getProgressMap(userId, units);

        return units.stream()
                .map(unit -> {
                    UserUnitProgress progress = progressMap.get(unit.getId());
                    if (progress == null) {
                        throw new NotFoundException("Unit progress not found");
                    }
                    return new UnitWithProgress(unit, progress);
                })
                .filter(up -> !up.progress().isCompleted())
                .findFirst()
                .orElse(null);
    }

    private Map<UUID, UserUnitProgress> getProgressMap(UUID userId, List<Unit> units) {
        List<UUID> unitIds = units.stream()
                .map(Unit::getId)
                .toList();

        return userUnitProgressRepository
                .findByUserIdAndUnitIdIn(userId, unitIds)
                .stream()
                .collect(Collectors.toMap(
                        p -> p.getUnit().getId(),
                        Function.identity()
                ));
    }

    private UUID findNextExerciseInUnit(UserUnitProgress progress, List<Exercise> exercises) {
        int nextIndex = progress.getCompletedExercises();

        if (nextIndex >= exercises.size()) {
            throw new NotFoundException("No next exercise");
        }

        return exercises.get(nextIndex).getId();
    }

    private Map<UUID, List<Exercise>> getExercisesMap(List<Unit> units) {
        List<UUID> unitIds = units.stream()
                .map(Unit::getId)
                .toList();

        return exerciseRepository
                .findByUnitIdInOrderByOrderIndex(unitIds)
                .stream()
                .collect(Collectors.groupingBy(
                        ex -> ex.getUnit().getId()
                ));
    }
    private UserUnitProgress getUnitProgressOrThrow(UUID userId, UUID unitId) {
        return userUnitProgressRepository
                .findByUserIdAndUnitId(userId, unitId)
                .orElseThrow(() -> new NotFoundException("Unit progress not found"));
    }
    private NextStepResponse buildNextStepResponse(NextStepType nextStepType, UUID nextExerciseId) {
        return NextStepResponse.builder()
                .type(nextStepType)
                .id(nextExerciseId)
                .build();
    }
}