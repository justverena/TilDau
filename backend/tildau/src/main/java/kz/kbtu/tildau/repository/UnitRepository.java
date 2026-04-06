package kz.kbtu.tildau.repository;

import kz.kbtu.tildau.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UnitRepository extends JpaRepository<Unit, UUID> {
    List<Unit> findByCourseIdOrderByOrderIndex(UUID courseId);
}