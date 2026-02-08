package kz.kbtu.tildau.repository;

import kz.kbtu.tildau.entity.CourseUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseUnitRepository extends JpaRepository<CourseUnit, UUID> {
    List<CourseUnit> findByCourseId(UUID courseId);
}