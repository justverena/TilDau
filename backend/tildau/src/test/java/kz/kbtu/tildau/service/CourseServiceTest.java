package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.course.*;
import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {
    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserDefectTypeRepository userDefectTypeRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseUnitRepository courseUnitRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private CourseService courseService;

    private UUID userId;
    private DefectType articulationDefect;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userId = UUID.randomUUID();
        articulationDefect = new DefectType();
        articulationDefect.setId(1);
        articulationDefect.setName("articulation");
    }

    @Test
    void getCoursesForUser_Success() {
        User user = new User();
        user.setId(userId);

        UserDefectType userDefectType = new UserDefectType();
        userDefectType.setUser(user);
        userDefectType.setDefectType(articulationDefect);

        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setTitle("Articulation");
        course.setDescription("Articulation basics course");
        course.setDefectType(articulationDefect);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
        when(courseRepository.findByDefectTypeId(articulationDefect.getId())).thenReturn(List.of(course));

        List<CourseShortResponse> result = courseService.getCoursesForUser(userId);

        assertEquals(1, result.size());
        assertEquals(course.getId(),result.getFirst().getId());
        assertEquals("Articulation", result.getFirst().getTitle());

    }

    @Test
    void getCoursesForUser_Fails_WhenUserHasNoDefectType() {
        User user = new User();
        user.setId(userId);

        when(userJpaRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> courseService.getCoursesForUser(userId));

        assertEquals("User defect type not found", ex.getMessage());
    }

    @Test
    void getCourseForUser_Success() {
        UUID courseId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        UserDefectType userDefectType = new UserDefectType();
        userDefectType.setUser(user);
        userDefectType.setDefectType(articulationDefect);

        Course course = new Course();
        course.setId(courseId);
        course.setTitle("Articulation basics");
        course.setDescription("Full course");
        course.setDefectType(articulationDefect);

        CourseUnit unit = new CourseUnit();
        unit.setId(UUID.randomUUID());
        unit.setTitle("UnitResponse 1");
        unit.setDescription("Intro");

        Exercise exercise = new Exercise();
        exercise.setId(UUID.randomUUID());
        exercise.setTitle("ExerciseResponse 1");
        exercise.setInstruction("Simple exercise");

        when(userJpaRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId))
                .thenReturn(Optional.of(userDefectType));
        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));
        when(courseUnitRepository.findByCourseId(courseId))
                .thenReturn(List.of(unit));
        when(exerciseRepository.findByUnitId(unit.getId()))
                .thenReturn(List.of(exercise));

        CourseFullResponse result = courseService.getCourseForUser(userId, courseId);

        assertEquals(courseId, result.getId());
        assertEquals(1, result.getUnits().size());
        assertEquals(1, result.getUnits().getFirst().getExercises().size());
    }

    @Test
    void getCourseForUser_Fails_WhenCourseHasDifferentDefectType() {
        UUID courseId = UUID.randomUUID();

        DefectType stuttering = new DefectType();
        stuttering.setId(2);
        stuttering.setName("stuttering");

        User user = new User();
        user.setId(userId);

        UserDefectType userDefectType = new UserDefectType();
        userDefectType.setUser(user);
        userDefectType.setDefectType(articulationDefect);

        Course course2 = new Course();
        course2.setId(courseId);
        course2.setDefectType(stuttering);

        when(userJpaRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId))
                .thenReturn(Optional.of(userDefectType));
        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course2));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> courseService.getCourseForUser(userId, courseId));

        assertEquals("Course does not belong to user's defect type", ex.getMessage());
    }
}