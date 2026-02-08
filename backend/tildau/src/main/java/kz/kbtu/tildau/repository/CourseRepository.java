package kz.kbtu.tildau.repository;

import kz.kbtu.tildau.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByDefectTypeId(Integer defectTypeId);
}