//package kz.kbtu.tildau.service;
//
//import kz.kbtu.tildau.dto.course.*;
//import kz.kbtu.tildau.dto.nextStep.NextStepResponse;
//import kz.kbtu.tildau.entity.*;
//import kz.kbtu.tildau.enums.NextStepType;
//import kz.kbtu.tildau.exception.ForbiddenException;
//import kz.kbtu.tildau.exception.NotFoundException;
//import kz.kbtu.tildau.repository.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class CourseServiceTest {
//    @Mock private UserJpaRepository userJpaRepository;
//    @Mock private UserDefectTypeRepository userDefectTypeRepository;
//    @Mock private CourseRepository courseRepository;
//    @Mock private UnitRepository unitRepository;
//    @Mock private ExerciseRepository exerciseRepository;
//    @InjectMocks private CourseService courseService;
//    @Mock private UserCourseProgressRepository userCourseProgressRepository;
//    @Mock private UserUnitProgressRepository userUnitProgressRepository;
//    @Mock private UserExerciseRepository userExerciseRepository;
//
//    private UUID userId;
//    private DefectType articulationDefect;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.initMocks(this);
//        userId = UUID.randomUUID();
//        articulationDefect = new DefectType();
//        articulationDefect.setId(1);
//        articulationDefect.setName("articulation");
//    }
//
//    @Test
//    void getCoursesForUser_Success() {
//        User user = new User();
//        user.setId(userId);
//
//        UserDefectType userDefectType = new UserDefectType();
//        userDefectType.setUser(user);
//        userDefectType.setDefectType(articulationDefect);
//
//        Course course = new Course();
//        course.setId(UUID.randomUUID());
//        course.setTitle("Articulation");
//        course.setDescription("Articulation basics course");
//        course.setDefectType(articulationDefect);
//
//        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
//        when(courseRepository.findByDefectTypeId(articulationDefect.getId())).thenReturn(List.of(course));
//
//        List<CourseShortResponse> result = courseService.getCoursesForUser(userId);
//
//        assertEquals(1, result.size());
//        assertEquals(course.getId(),result.getFirst().getId());
//        assertEquals("Articulation", result.getFirst().getTitle());
//
//    }
//
//    @Test
//    void getCoursesForUser_Fails_WhenUserHasNoDefectType() {
//        User user = new User();
//        user.setId(userId);
//
//        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.empty());
//
//        NotFoundException ex = assertThrows(NotFoundException.class, () -> courseService.getCoursesForUser(userId));
//
//        assertEquals("User defect type not found", ex.getMessage());
//    }
//
//    @Test
//    void getCourseForUser_Success() {
//        UUID courseId = UUID.randomUUID();
//
//        User user = new User();
//        user.setId(userId);
//
//        UserDefectType userDefectType = new UserDefectType();
//        userDefectType.setUser(user);
//        userDefectType.setDefectType(articulationDefect);
//
//        Course course = new Course();
//        course.setId(courseId);
//        course.setTitle("Articulation basics");
//        course.setDescription("Full course");
//        course.setDefectType(articulationDefect);
//
//        Unit unit = new Unit();
//        unit.setId(UUID.randomUUID());
//        unit.setTitle("UnitResponse 1");
//        unit.setDescription("Intro");
//
//        Exercise exercise = new Exercise();
//        exercise.setId(UUID.randomUUID());
//        exercise.setTitle("ExerciseResponse 1");
//        exercise.setInstruction("Simple exercise");
//
//        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
//        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
//        when(unitRepository.findByCourseIdOrderByOrderIndex(courseId)).thenReturn(List.of(unit));
//        when(exerciseRepository.findByUnitIdOrderByOrderIndex(unit.getId())).thenReturn(List.of(exercise));
//
//        CourseFullResponse result = courseService.getCourseForUser(userId, courseId);
//
//        assertEquals(courseId, result.getId());
//        assertEquals(1, result.getUnits().size());
//        assertEquals(1, result.getUnits().getFirst().getExercises().size());
//    }
//
//    @Test
//    void getCourseForUser_Fails_WhenCourseHasDifferentDefectType() {
//        UUID courseId = UUID.randomUUID();
//
//        DefectType stuttering = new DefectType();
//        stuttering.setId(2);
//        stuttering.setName("stuttering");
//
//        User user = new User();
//        user.setId(userId);
//
//        UserDefectType userDefectType = new UserDefectType();
//        userDefectType.setUser(user);
//        userDefectType.setDefectType(articulationDefect);
//
//        Course course2 = new Course();
//        course2.setId(courseId);
//        course2.setDefectType(stuttering);
//
//        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
//        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course2));
//
//        RuntimeException ex = assertThrows(RuntimeException.class, () -> courseService.getCourseForUser(userId, courseId));
//
//        assertEquals("Course does not belong to user's defect type", ex.getMessage());
//    }
//
//    @Test
//    void startCourse_StartsCourseAndReturnsFirstExercise() {
//        UUID courseId = UUID.randomUUID();
//
//        User user = new User();
//        user.setId(userId);
//
//        UserDefectType userDefectType = new UserDefectType();
//        userDefectType.setUser(user);
//        userDefectType.setDefectType(articulationDefect);
//
//        Course course = new Course();
//        course.setId(courseId);
//        course.setDefectType(articulationDefect);
//
//        Unit unit = new Unit();
//        unit.setId(UUID.randomUUID());
//
//        Exercise ex1 = new Exercise();
//        ex1.setId(UUID.randomUUID());
//
//        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
//        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
//        when(userCourseProgressRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.empty());
//        when(unitRepository.findByCourseIdOrderByOrderIndex(courseId)).thenReturn(List.of(unit));
//        when(exerciseRepository.findByUnitIdOrderByOrderIndex(unit.getId())).thenReturn(List.of(ex1));
//        when(userUnitProgressRepository.findByUserIdAndUnitId(userId, unit.getId())).thenReturn(Optional.of(new UserUnitProgress()));
//        when(userExerciseRepository.findByUserIdAndExerciseIdIn(eq(userId), anyList())).thenReturn(List.of());
//
//        NextStepResponse result = courseService.startCourse(userId, courseId);
//
//        assertEquals(NextStepType.EXERCISE, result.getType());
//        assertEquals(ex1.getId(), result.getId());
//
//        verify(userCourseProgressRepository).save(any(UserCourseProgress.class));
//        verify(userUnitProgressRepository).save(any(UserUnitProgress.class));
//    }
//
//    @Test
//    void startCourse_ResumesCourseAndReturnsNextExercise() {
//        UUID courseId = UUID.randomUUID();
//
//        User user = new User();
//        user.setId(userId);
//
//        UserDefectType userDefectType = new UserDefectType();
//        userDefectType.setUser(user);
//        userDefectType.setDefectType(articulationDefect);
//
//        Course course = new Course();
//        course.setId(courseId);
//        course.setDefectType(articulationDefect);
//
//        Unit unit = new Unit();
//        unit.setId(UUID.randomUUID());
//
//        Exercise ex1 = new Exercise();
//        ex1.setId(UUID.randomUUID());
//
//        UserCourseProgress progress = new UserCourseProgress();
//
//        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
//        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
//        when(userCourseProgressRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(progress));
//        when(unitRepository.findByCourseIdOrderByOrderIndex(courseId)).thenReturn(List.of(unit));
//        when(userUnitProgressRepository.findByUserIdAndUnitId(userId, unit.getId())).thenReturn(Optional.of(new UserUnitProgress()));
//        when(exerciseRepository.findByUnitIdOrderByOrderIndex(unit.getId())).thenReturn(List.of(ex1));
//        when(userExerciseRepository.findByUserIdAndExerciseIdIn(eq(userId), anyList())).thenReturn(List.of());
//
//        NextStepResponse result = courseService.startCourse(userId, courseId);
//
//        assertEquals(NextStepType.EXERCISE, result.getType());
//        assertEquals(ex1.getId(), result.getId());
//
//        verify(userCourseProgressRepository, never()).save(any());
//        verify(userUnitProgressRepository, never()).save(any());
//    }
//
//    @Test
//    void startCourse_Fails_WhenCourseNotFound() {
//        UUID courseId = UUID.randomUUID();
//
//        User user = new User();
//        user.setId(userId);
//
//        UserDefectType userDefectType = new UserDefectType();
//        userDefectType.setUser(user);
//        userDefectType.setDefectType(articulationDefect);
//
//        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
//        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());
//
//        NotFoundException ex = assertThrows(NotFoundException.class,
//                () -> courseService.startCourse(userId, courseId));
//
//        assertEquals("Course not found", ex.getMessage());
//    }
//
//    @Test
//    void startCourse_Fails_WhenCourseCompleted() {
//        UUID courseId = UUID.randomUUID();
//
//        User user = new User();
//        user.setId(userId);
//
//        UserDefectType userDefectType = new UserDefectType();
//        userDefectType.setUser(user);
//        userDefectType.setDefectType(articulationDefect);
//
//        Course course = new Course();
//        course.setId(courseId);
//        course.setDefectType(articulationDefect);
//
//        Unit unit = new Unit();
//        unit.setId(UUID.randomUUID());
//
//        Exercise ex1 = new Exercise();
//        ex1.setId(UUID.randomUUID());
//
//        UserUnitProgress progress = new UserUnitProgress();
//        progress.setCompleted(true);
//
//        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
//        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
//        when(unitRepository.findByCourseIdOrderByOrderIndex(courseId)).thenReturn(List.of(unit));
//        when(userCourseProgressRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(new UserCourseProgress()));
//        when(userUnitProgressRepository.findByUserIdAndUnitId(userId, unit.getId())).thenReturn(Optional.of(progress));
//
//        ForbiddenException ex = assertThrows(ForbiddenException.class,
//                () -> courseService.startCourse(userId, courseId));
//
//        assertEquals("Course already completed", ex.getMessage());
//    }
//
//    @Test
//    void startCourse_Fails_WhenUserNotFound() {
//        UUID courseId = UUID.randomUUID();
//
//        when(userJpaRepository.findById(userId)).thenReturn(Optional.empty());
//
//        NotFoundException ex = assertThrows(NotFoundException.class,
//                () -> courseService.startCourse(userId, courseId));
//
//        assertEquals("User not found", ex.getMessage());
//    }
//
//    @Test
//    void startCourse_Fails_WhenCourseDefectTypeMismatch() {
//        UUID courseId = UUID.randomUUID();
//
//        User user = new User();
//        user.setId(userId);
//
//        DefectType anotherDefect = new DefectType();
//        anotherDefect.setId(2);
//
//        UserDefectType userDefectType = new UserDefectType();
//        userDefectType.setUser(user);
//        userDefectType.setDefectType(articulationDefect);
//
//        Course course = new Course();
//        course.setId(courseId);
//        course.setDefectType(anotherDefect);
//
//        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
//        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
//
//        ForbiddenException ex = assertThrows(ForbiddenException.class,
//                () -> courseService.startCourse(userId, courseId));
//
//        assertEquals("Course does not belong to user's defect type", ex.getMessage());
//    }
//
//}