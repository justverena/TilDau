package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.course.*;
import kz.kbtu.tildau.dto.nextStep.NextStepResponse;
import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.enums.NextStepType;
import kz.kbtu.tildau.exception.ForbiddenException;
import kz.kbtu.tildau.exception.NotFoundException;
import kz.kbtu.tildau.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {
    @Mock private UserJpaRepository userJpaRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private UnitRepository unitRepository;
    @Mock private ExerciseRepository exerciseRepository;
    @InjectMocks private CourseService courseService;
    @Mock private UserCourseProgressRepository userCourseProgressRepository;
    @Mock private UserUnitProgressRepository userUnitProgressRepository;
    @Mock private NextStepService nextStepService;
    @Mock private UserDefectTypeService userDefectTypeService;

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

        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setTitle("Articulation");
        course.setDescription("Articulation basics course");

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeService.getUserDefectOrThrow(userId)).thenReturn(articulationDefect);
        when(courseRepository.findByDefectTypeId(articulationDefect.getId())).thenReturn(List.of(course));

        List<CourseShortResponse> result = courseService.getCoursesForUser(userId);

        assertEquals(1, result.size());
        assertEquals(course.getId(),result.getFirst().getId());
        assertEquals("Articulation", result.getFirst().getTitle());

    }

    @Test
    void getCourseForUser_Success() {
        UUID courseId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Course course = new Course();
        course.setId(courseId);
        course.setTitle("Articulation basics");
        course.setDescription("Full course");
        course.setDefectType(articulationDefect);

        Unit unit = new Unit();
        unit.setId(UUID.randomUUID());
        unit.setTitle("UnitResponse 1");
        unit.setDescription("Intro");
        unit.setCourse(course);
        unit.setOrderIndex(1);

        Exercise exercise = new Exercise();
        exercise.setId(UUID.randomUUID());
        exercise.setUnit(unit);
        exercise.setTitle("ExerciseResponse 1");
        exercise.setInstruction("Simple exercise");
        exercise.setOrderIndex(1);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeService.getUserDefectOrThrow(userId)).thenReturn(articulationDefect);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(unitRepository.findByCourseIdOrderByOrderIndex(courseId)).thenReturn(List.of(unit));
        when(exerciseRepository.findByUnitIdInOrderByOrderIndex(any())).thenReturn(List.of(exercise));
        when(userUnitProgressRepository.findByUserIdAndUnitIdIn(any(), any())).thenReturn(List.of());

        CourseFullResponse result = courseService.getCourseForUser(userId, courseId);

        assertEquals(courseId, result.getId());
        assertEquals(1, result.getUnits().size());
        assertEquals(1, result.getUnits().getFirst().getExercises().size());
        assertEquals(BigDecimal.ZERO, result.getProgressPercent());
    }

    @Test
    void startCourse_StartsCourseAndReturnsFirstExercise() {
        UUID courseId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Course course = new Course();
        course.setId(courseId);
        course.setDefectType(articulationDefect);

        Unit unit = new Unit();
        unit.setId(UUID.randomUUID());

        Exercise ex1 = new Exercise();
        ex1.setId(UUID.randomUUID());

        UserUnitProgress unitProgress = new UserUnitProgress();
        unitProgress.setCompleted(false);
        unitProgress.setCompletedExercises(0);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeService.getUserDefectOrThrow(userId)).thenReturn(articulationDefect);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        when(userCourseProgressRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.empty());
        when(unitRepository.findByCourseIdOrderByOrderIndex(courseId)).thenReturn(List.of(unit));
        when(userUnitProgressRepository.findByUserIdAndUnitIdIn(eq(userId), anyList())).thenReturn(List.of(unitProgress));
        when(exerciseRepository.findByUnitIdInOrderByOrderIndex(anyList())).thenReturn(List.of(ex1));
        when(nextStepService.getNextStep(any(), any())).thenReturn(new NextStepResponse(NextStepType.EXERCISE, UUID.randomUUID()));

        NextStepResponse result = courseService.startCourse(userId, courseId);

        assertEquals(NextStepType.EXERCISE, result.getType());

        verify(userCourseProgressRepository).save(any());
        verify(userUnitProgressRepository).save(any());
        verify(nextStepService).getNextStep(userId, courseId);
    }

    @Test
    void startCourse_ResumesCourseAndReturnsNextExercise() {
        UUID courseId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Course course = new Course();
        course.setId(courseId);
        course.setDefectType(articulationDefect);

        Unit unit = new Unit();
        unit.setId(UUID.randomUUID());

        Exercise ex1 = new Exercise();
        ex1.setId(UUID.randomUUID());

        UserUnitProgress unitProgress = new UserUnitProgress();
        unitProgress.setCompleted(false);
        unitProgress.setCompletedExercises(0);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeService.getUserDefectOrThrow(userId)).thenReturn(articulationDefect);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        when(userCourseProgressRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(new UserCourseProgress()));
        when(unitRepository.findByCourseIdOrderByOrderIndex(courseId)).thenReturn(List.of(unit));
        when(userUnitProgressRepository.findByUserIdAndUnitIdIn(eq(userId), anyList())).thenReturn(List.of(unitProgress));
        when(exerciseRepository.findByUnitIdInOrderByOrderIndex(anyList())).thenReturn(List.of(ex1));
        when(nextStepService.getNextStep(any(), any())).thenReturn(new NextStepResponse(NextStepType.EXERCISE, UUID.randomUUID()));

        NextStepResponse result = courseService.startCourse(userId, courseId);

        assertEquals(NextStepType.EXERCISE, result.getType());

        verify(nextStepService).getNextStep(userId, courseId);
        verify(userCourseProgressRepository, never()).save(any());
        verify(userUnitProgressRepository, never()).save(any());
    }

    @Test
    void startCourse_Fails_WhenCourseNotFound() {
        UUID courseId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeService.getUserDefectOrThrow(userId)).thenReturn(articulationDefect);
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> courseService.startCourse(userId, courseId));

        assertEquals("Course not found", ex.getMessage());
    }

    @Test
    void startCourse_Fails_WhenCourseCompleted() {
        UUID courseId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        Course course = new Course();
        course.setId(courseId);
        course.setDefectType(articulationDefect);

        Unit unit = new Unit();
        unit.setId(UUID.randomUUID());

        UserUnitProgress progress = new UserUnitProgress();
        progress.setCompleted(true);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeService.getUserDefectOrThrow(userId)).thenReturn(articulationDefect);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        when(unitRepository.findByCourseIdOrderByOrderIndex(courseId)).thenReturn(List.of(unit));
        when(userCourseProgressRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(new UserCourseProgress()));
        when(userUnitProgressRepository.findByUserIdAndUnitIdIn(eq(userId), anyList())).thenReturn(List.of(progress));
        when(nextStepService.getNextStep(any(), any())).thenThrow(new ForbiddenException("Course already completed"));

        ForbiddenException ex = assertThrows(ForbiddenException.class,
                () -> courseService.startCourse(userId, courseId));


        assertEquals("Course already completed", ex.getMessage());
    }

    @Test
    void startCourse_Fails_WhenUserNotFound() {
        UUID courseId = UUID.randomUUID();

        when(userJpaRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> courseService.startCourse(userId, courseId));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void startCourse_Fails_WhenUnitProgressMissing() {
        UUID courseId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Course course = new Course();
        course.setId(courseId);
        course.setDefectType(articulationDefect);

        Unit unit = new Unit();
        unit.setId(UUID.randomUUID());

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeService.getUserDefectOrThrow(userId)).thenReturn(articulationDefect);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        when(unitRepository.findByCourseIdOrderByOrderIndex(courseId)).thenReturn(List.of(unit));
        when(userUnitProgressRepository.findByUserIdAndUnitIdIn(eq(userId), anyList())).thenReturn(List.of());
        when(nextStepService.getNextStep(any(), any())).thenThrow(new NotFoundException("Unit progress not found"));

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> courseService.startCourse(userId, courseId));

        assertEquals("Unit progress not found", ex.getMessage());
    }
}