package kz.kbtu.tildau.repository;

import kz.kbtu.tildau.entity.UserUnitProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserUnitProgressRepository extends JpaRepository<UserUnitProgress, UUID> {
    Optional<UserUnitProgress> findByUserIdAndUnitId(UUID userId, UUID unitId);
}
