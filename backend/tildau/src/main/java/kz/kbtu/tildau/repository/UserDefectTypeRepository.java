package kz.kbtu.tildau.repository;

import kz.kbtu.tildau.entity.UserDefectType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserDefectTypeRepository extends JpaRepository<UserDefectType, UUID> {
    Optional<UserDefectType> findByUserId(UUID userId);
}