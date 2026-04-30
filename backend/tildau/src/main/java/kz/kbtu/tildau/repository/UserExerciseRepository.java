package kz.kbtu.tildau.repository;

import kz.kbtu.tildau.dto.stats.ActivityDayResponse;
import kz.kbtu.tildau.entity.UserExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface UserExerciseRepository extends JpaRepository<UserExercise, UUID> {
    int countByUserIdAndExerciseId(UUID userId, UUID exerciseId);
}
