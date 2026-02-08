package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.course.*;
import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.exception.NotFoundException;
import kz.kbtu.tildau.exception.UnauthorizedException;
import kz.kbtu.tildau.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final UserJpaRepository userJpaRepository;
    private final UserDefectTypeRepository userDefectTypeRepository;
    private final CourseRepository courseRepository;
    private final CourseUnitRepository courseUnitRepository;
    private final ExerciseRepository exerciseRepository;

    public CourseService(UserJpaRepository userJpaRepository,UserDefectTypeRepository userDefectTypeRepository,
                         CourseRepository courseRepository, CourseUnitRepository courseUnitRepository,
                         ExerciseRepository exerciseRepository) {
        this.userJpaRepository = userJpaRepository;
        this.userDefectTypeRepository = userDefectTypeRepository;
        this.courseRepository = courseRepository;
        this.courseUnitRepository = courseUnitRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public List<CourseShortResponse> getCoursesForUser(UUID userId) {

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserDefectType userDefectType = userDefectTypeRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User defect type not found"));

        Integer defectTypeId = userDefectType.getDefectType().getId();

        return courseRepository.findByDefectTypeId(defectTypeId)
                .stream()
                .map(course -> new CourseShortResponse(
                        course.getId(),
                        course.getTitle(),
                        course.getDescription()
                ))
                .collect(Collectors.toList());
    }

    public CourseFullResponse getCourseForUser(UUID userId, UUID courseId) {

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserDefectType userDefectType = userDefectTypeRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User defect type not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));

        if (!course.getDefectType().getId()
                .equals(userDefectType.getDefectType().getId())) {
            throw new UnauthorizedException("Course does not belong to user's defect type");
        }

        List<UnitResponse> units = courseUnitRepository.findByCourseId(courseId)
                .stream()
                .map(unit -> {
                    List<ExerciseResponse> exercises =
                            exerciseRepository.findByUnitId(unit.getId())
                                    .stream()
                                    .map(ex -> new ExerciseResponse(
                                            ex.getId(),
                                            ex.getTitle(),
                                            ex.getInstruction()
                                    ))
                                    .toList();

                    return new UnitResponse(
                            unit.getId(),
                            unit.getTitle(),
                            unit.getDescription(),
                            exercises
                    );
                })
                .toList();

        return new CourseFullResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                units
        );
    }
}