package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.exercise.ExerciseFullResponse;
import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.enums.ExerciseType;
import kz.kbtu.tildau.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserDefectTypeRepository userDefectTypeRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private CourseUnitRepository courseUnitRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private ExerciseService exerciseService;

    private UUID userId;
    private UUID exerciseId;
    private DefectType articulationDefect;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        exerciseId = UUID.randomUUID();

        articulationDefect = new DefectType();
        articulationDefect.setId(1);
        articulationDefect.setName("articulation");
    }

    @Test
    void getExerciseForUser_ReadAloud_Success() {

        User user = new User();
        user.setId(userId);

        UserDefectType userDefectType = new UserDefectType();
        userDefectType.setUser(user);
        userDefectType.setDefectType(articulationDefect);

        Course course = new Course();
        course.setDefectType(articulationDefect);

        CourseUnit unit = new CourseUnit();
        unit.setCourse(course);

        Exercise exercise = new Exercise();
        exercise.setId(exerciseId);
        exercise.setExerciseType(ExerciseType.READ_ALOUD);
        exercise.setExpectedText("Some text");
        exercise.setUnit(unit);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
        when(exerciseRepository
                .findByIdAndUnit_Course_DefectType_Id(exerciseId, articulationDefect.getId()))
                .thenReturn(Optional.of(exercise));

        ExerciseFullResponse result =
                exerciseService.getExercise(userId, exerciseId);

        assertEquals(ExerciseType.READ_ALOUD, result.getExerciseType());
        assertEquals("Some text", result.getExpectedText());
        assertNull(result.getReferenceAudioUrl());
    }

    @Test
    void getExerciseForUser_RepeatAfterAudio_Success() {

        User user = new User();
        user.setId(userId);

        UserDefectType userDefectType = new UserDefectType();
        userDefectType.setUser(user);
        userDefectType.setDefectType(articulationDefect);

        Course course = new Course();
        course.setDefectType(articulationDefect);

        CourseUnit unit = new CourseUnit();
        unit.setCourse(course);

        Exercise exercise = new Exercise();
        exercise.setId(exerciseId);
        exercise.setExerciseType(ExerciseType.REPEAT_AFTER_AUDIO);
        exercise.setExpectedText("Hidden text");
        exercise.setReferenceAudioUrl("exercise/audio.wav");
        exercise.setUnit(unit);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
        when(exerciseRepository
                .findByIdAndUnit_Course_DefectType_Id(exerciseId, articulationDefect.getId()))
                .thenReturn(Optional.of(exercise));
        when(minioService.getPresignedUrl("exercise/audio.wav"))
                .thenReturn("https://presigned-url");

        ExerciseFullResponse result =
                exerciseService.getExercise(userId, exerciseId);

        assertEquals(ExerciseType.REPEAT_AFTER_AUDIO, result.getExerciseType());
        assertNull(result.getExpectedText());
        assertEquals("https://presigned-url", result.getReferenceAudioUrl());
    }

    @Test
    void getExerciseForUser_Fails_WhenUserNotFound() {

        when(userJpaRepository.findById(userId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> exerciseService.getExercise(userId, exerciseId));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getExerciseForUser_Fails_WhenUserHasNoDefectType() {

        User user = new User();
        user.setId(userId);

        when(userJpaRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> exerciseService.getExercise(userId, exerciseId));

        assertEquals("User defect type not found", ex.getMessage());
    }

    @Test
    void getExerciseForUser_Fails_WhenExerciseHasDifferentDefectType() {

        User user = new User();
        user.setId(userId);

        UserDefectType userDefectType = new UserDefectType();
        userDefectType.setUser(user);
        userDefectType.setDefectType(articulationDefect);

        when(userJpaRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userDefectTypeRepository.findByUserId(userId))
                .thenReturn(Optional.of(userDefectType));

        when(exerciseRepository
                .findByIdAndUnit_Course_DefectType_Id(
                        exerciseId,
                        articulationDefect.getId()
                ))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> exerciseService.getExercise(userId, exerciseId));

        assertEquals("Exercise does not belong to user's defect type",
                ex.getMessage());
    }
}