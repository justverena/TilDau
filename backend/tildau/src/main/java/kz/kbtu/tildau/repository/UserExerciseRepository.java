package kz.kbtu.tildau.repository;

import kz.kbtu.tildau.entity.UserExercise;
import kz.kbtu.tildau.enums.ExerciseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserExerciseRepository extends JpaRepository<UserExercise, UUID> {
    int countByUserIdAndExerciseId(UUID userId, UUID exerciseId);
    List<UserExercise> findByUserIdAndExerciseIdIn(UUID userId, List<UUID> exerciseIds);
}
