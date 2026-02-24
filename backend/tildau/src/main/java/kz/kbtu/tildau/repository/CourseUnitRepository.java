package kz.kbtu.tildau.repository;

import kz.kbtu.tildau.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseUnitRepository extends JpaRepository<Unit, UUID> {
    List<Unit> findByCourseId(UUID courseId);
}