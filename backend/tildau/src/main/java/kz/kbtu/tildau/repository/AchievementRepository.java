package kz.kbtu.tildau.repository;

import kz.kbtu.tildau.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
    Optional<Achievement> findByCode(String code);
}