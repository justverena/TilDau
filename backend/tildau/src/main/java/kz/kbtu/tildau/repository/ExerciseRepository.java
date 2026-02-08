package kz.kbtu.tildau.repository;

import kz.kbtu.tildau.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    List<Exercise> findByUnitId(UUID unitId);
}