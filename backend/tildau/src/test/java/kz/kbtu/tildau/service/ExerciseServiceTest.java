package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.ai.AnalyzeResponse;
import kz.kbtu.tildau.dto.exercise.ExerciseFullResponse;
import kz.kbtu.tildau.dto.exercise.SubmitExerciseResponse;
import kz.kbtu.tildau.dto.nextStep.NextStepResponse;
import kz.kbtu.tildau.embeddedId.UserDefectTypeId;
import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.enums.ExerciseStatus;
import kz.kbtu.tildau.enums.ExerciseType;
import kz.kbtu.tildau.enums.NextStepType;
import kz.kbtu.tildau.exception.NotFoundException;
import kz.kbtu.tildau.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock private UserJpaRepository userJpaRepository;
    @Mock private UserDefectTypeRepository userDefectTypeRepository;
    @Mock private ExerciseRepository exerciseRepository;
    @Mock private MinioService minioService;
    @Mock private UserExerciseRepository userExerciseRepository;
    @Mock private AiAnalysisResultRepository aiAnalysisResultRepository;
    @Mock private AiService aiService;
    @Mock private ProgressService progressService;
    @Mock private NextStepService nextStepService;

    @InjectMocks private ExerciseService exerciseService;

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

    private UserDefectType createUserDefectType(User user, DefectType defectType) {
        UserDefectTypeId embeddedId = UserDefectTypeId.builder().userId(user.getId()).build();
        return UserDefectType.builder()
                .id(embeddedId)
                .user(user)
                .defectType(defectType)
                .build();
    }

    private Exercise createExercise(UUID exerciseId, ExerciseType type, String expectedText, Course course) {
        Unit unit = new Unit();
        unit.setCourse(course);
        Exercise exercise = new Exercise();
        exercise.setId(exerciseId);
        exercise.setExerciseType(type);
        exercise.setExpectedText(expectedText);
        exercise.setUnit(unit);
        return exercise;
    }

    private Course createCourse(DefectType defectType) {
        Course course = new Course();
        course.setDefectType(defectType);
        return course;
    }

    @Test
    void getExercise_ReadAloud_Success() {
        User user = new User();
        user.setId(userId);

        UserDefectType userDefectType = createUserDefectType(user, articulationDefect);
        Course course = createCourse(articulationDefect);
        Exercise exercise = createExercise(exerciseId, ExerciseType.READ_ALOUD, "Some text", course);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
        when(exerciseRepository.findByIdAndUnit_Course_DefectType_Id(exerciseId, articulationDefect.getId())).thenReturn(Optional.of(exercise));

        ExerciseFullResponse result = exerciseService.getExercise(userId, exerciseId);

        assertEquals(ExerciseType.READ_ALOUD, result.getExerciseType());
        assertEquals("Some text", result.getExpectedText());
        assertNull(result.getReferenceAudioUrl());
        verify(progressService).validateExerciseAccess(user, exercise);
    }

    @Test
    void getExercise_RepeatAfterAudio_Success() {
        User user = new User();
        user.setId(userId);
        UserDefectType userDefectType = createUserDefectType(user, articulationDefect);
        Course course = createCourse(articulationDefect);
        Exercise exercise = createExercise(exerciseId, ExerciseType.REPEAT_AFTER_AUDIO, "Hidden text", course);
        exercise.setReferenceAudioUrl("exercise/audio.wav");

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
        when(exerciseRepository.findByIdAndUnit_Course_DefectType_Id(exerciseId, articulationDefect.getId())).thenReturn(Optional.of(exercise));
        when(minioService.getPresignedUrl("exercise/audio.wav")).thenReturn("https://presigned-url");

        ExerciseFullResponse result = exerciseService.getExercise(userId, exerciseId);

        assertEquals(ExerciseType.REPEAT_AFTER_AUDIO, result.getExerciseType());
        assertNull(result.getExpectedText());
        assertEquals("https://presigned-url", result.getReferenceAudioUrl());
        verify(progressService).validateExerciseAccess(user, exercise);
    }

    @Test
    void getExercise_UserNotFound_Throws() {
        when(userJpaRepository.findById(userId)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> exerciseService.getExercise(userId, exerciseId));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getExercise_UserHasNoDefectType_Throws() {
        User user = new User();
        user.setId(userId);
        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> exerciseService.getExercise(userId, exerciseId));
        assertEquals("User defect type not found", ex.getMessage());
    }

    @Test
    void getExercise_ExerciseWrongDefectType_Throws() {
        User user = new User(); user.setId(userId);
        UserDefectType userDefectType = createUserDefectType(user, articulationDefect);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
        when(exerciseRepository.findByIdAndUnit_Course_DefectType_Id(exerciseId, articulationDefect.getId())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> exerciseService.getExercise(userId, exerciseId));
        assertEquals("Exercise does not belong to user's defect type", ex.getMessage());
    }

    @Test
    void submitExercise_Success() {
        User user = new User(); user.setId(userId);
        UserDefectType userDefectType = createUserDefectType(user, articulationDefect);
        Course course = createCourse(articulationDefect);
        Exercise exercise = createExercise(exerciseId, ExerciseType.READ_ALOUD, "Expected text", course);
        AnalyzeResponse aiResponse = new AnalyzeResponse();
        aiResponse.setOverallScore(90);
        aiResponse.setFeedback(List.of("Work on pacing"));
        MultipartFile file = mock(MultipartFile.class);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
        when(exerciseRepository.findByIdAndUnit_Course_DefectType_Id(exerciseId, articulationDefect.getId())).thenReturn(Optional.of(exercise));
        when(userExerciseRepository.countByUserIdAndExerciseId(userId, exerciseId)).thenReturn(0);
        when(userExerciseRepository.save(any(UserExercise.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(aiService.analyze(any(), any())).thenReturn(aiResponse);
        when(nextStepService.getNextStepAfterExercise(any(), any())).thenReturn(new NextStepResponse(NextStepType.EXERCISE, UUID.randomUUID()));

        SubmitExerciseResponse response = exerciseService.submitExercise(userId, exerciseId, file);

        assertEquals(90, response.getOverallScore());
        assertEquals(List.of("Work on pacing"), response.getFeedback());
        verify(userExerciseRepository, atLeastOnce()).save(argThat(ue -> ue.getStatus() == ExerciseStatus.COMPLETED));
        verify(progressService).handleSuccessfulAttempt(user, exercise);
    }

    @Test
    void submitExercise_ExerciseNotFound_Throws() {
        MultipartFile file = mock(MultipartFile.class);
        User user = new User(); user.setId(userId);
        UserDefectType userDefectType = createUserDefectType(user, articulationDefect);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
        when(exerciseRepository.findByIdAndUnit_Course_DefectType_Id(exerciseId, articulationDefect.getId())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> exerciseService.submitExercise(userId, exerciseId, file));
        assertEquals("Exercise does not belong to user's defect type", ex.getMessage());
    }

    @Test
    void submitExercise_Fail_ReturnsRetry() {
        User user = new User(); user.setId(userId);
        UserDefectType userDefectType = createUserDefectType(user, articulationDefect);
        Course course = createCourse(articulationDefect);
        Exercise exercise = createExercise(exerciseId, ExerciseType.READ_ALOUD, "Expected text", course);
        AnalyzeResponse aiResponse = new AnalyzeResponse();
        aiResponse.setOverallScore(50);
        aiResponse.setFeedback(List.of("Work on pacing"));
        MultipartFile file = mock(MultipartFile.class);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
        when(exerciseRepository.findByIdAndUnit_Course_DefectType_Id(any(), anyInt())).thenReturn(Optional.of(exercise));
        when(aiService.analyze(any(), any())).thenReturn(aiResponse);
        when(userExerciseRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SubmitExerciseResponse response = exerciseService.submitExercise(userId, exerciseId, file);

        assertEquals(NextStepType.RETRY, response.getNextStep().getType());
        assertEquals(exerciseId, response.getNextStep().getId());

        verify(progressService, never()).handleSuccessfulAttempt(any(), any());
    }

    @Test
    void submitExercise_AttemptNumber_IncrementsCorrectly() throws Exception {
        User user = new User(); user.setId(userId);
        UserDefectType userDefectType = createUserDefectType(user, articulationDefect);
        Course course = createCourse(articulationDefect);
        Exercise exercise = createExercise(exerciseId, ExerciseType.READ_ALOUD, "Expected text", course);
        MultipartFile file = mock(MultipartFile.class);
        AnalyzeResponse aiResponse = new AnalyzeResponse();
        aiResponse.setOverallScore(95);
        aiResponse.setFeedback(List.of("Work on pacing"));

        when(userExerciseRepository.save(any(UserExercise.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
        when(exerciseRepository.findByIdAndUnit_Course_DefectType_Id(exerciseId, articulationDefect.getId())).thenReturn(Optional.of(exercise));
        when(userExerciseRepository.countByUserIdAndExerciseId(userId, exerciseId)).thenReturn(3);
        doNothing().when(minioService).putObject(any(), any());
        when(aiService.analyze(any(), any())).thenReturn(aiResponse);

        SubmitExerciseResponse response = exerciseService.submitExercise(userId, exerciseId, file);

        ArgumentCaptor<UserExercise> captor = ArgumentCaptor.forClass(UserExercise.class);
        verify(userExerciseRepository, atLeastOnce()).save(captor.capture());
        assertEquals(4, captor.getValue().getAttemptNumber());
        verify(progressService).validateExerciseAccess(user, exercise);
    }

    @Test
    void submitExercise_Fails_WhenAIIsNull() {
        User user = new User(); user.setId(userId);
        UserDefectType userDefectType = createUserDefectType(user, articulationDefect);
        Course course = createCourse(articulationDefect);
        Exercise exercise = createExercise(exerciseId, ExerciseType.READ_ALOUD, "Expected text", course);
        MultipartFile file = mock(MultipartFile.class);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
        when(exerciseRepository.findByIdAndUnit_Course_DefectType_Id(any(), anyInt())).thenReturn(Optional.of(exercise));
        when(aiService.analyze(any(), any())).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> exerciseService.submitExercise(userId, exerciseId, file));
    }

    @Test
    void submitExercise_FailsAndMarksAttemptFailed_OnException() {
        User user = new User(); user.setId(userId);
        UserDefectType userDefectType = createUserDefectType(user, articulationDefect);
        Course course = createCourse(articulationDefect);
        Exercise exercise = createExercise(exerciseId, ExerciseType.READ_ALOUD, "Expected text", course);
        MultipartFile file = mock(MultipartFile.class);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userDefectTypeRepository.findByUserId(userId)).thenReturn(Optional.of(userDefectType));
        when(exerciseRepository.findByIdAndUnit_Course_DefectType_Id(any(), anyInt())).thenReturn(Optional.of(exercise));
        when(aiService.analyze(any(), any())).thenThrow(new RuntimeException("AI failed"));

        assertThrows(RuntimeException.class,
                () -> exerciseService.submitExercise(userId, exerciseId, file));

        verify(userExerciseRepository, atLeastOnce()).save(any());
    }
}