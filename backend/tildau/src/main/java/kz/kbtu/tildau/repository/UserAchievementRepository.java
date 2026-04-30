package kz.kbtu.tildau.repository;

import kz.kbtu.tildau.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {
    List<UserAchievement> findAllByUserId(UUID userId);
    boolean existsByUserIdAndAchievement_Code(UUID userId, String code);
}