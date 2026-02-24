package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.ai.AnalyzeResponse;
import kz.kbtu.tildau.dto.exercise.ExerciseFullResponse;
import kz.kbtu.tildau.dto.exercise.SubmitExerciseRequest;
import kz.kbtu.tildau.dto.exercise.SubmitExerciseResponse;
import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.enums.ExerciseStatus;
import kz.kbtu.tildau.enums.ExerciseType;
import kz.kbtu.tildau.exception.NotFoundException;
import kz.kbtu.tildau.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ExerciseService {
    private final UserJpaRepository userRepository;
    private final UserExerciseRepository userExerciseRepository;
    private final AiAnalysisResultRepository aiAnalysisResultRepository;
    private final ExerciseRepository exerciseRepository;
    private final MinioService minioService;
    private final UserDefectTypeRepository userDefectTypeRepository;
    private final AiService aiService;
    private final ProgressService progressService;

    private static final int PASS_THRESHOLD = 85;

    public ExerciseFullResponse getExercise(UUID userId, UUID exerciseId) {
        User user = getUserOrThrow(userId);
        UserDefectType userDefectType = getUserDefectTypeOrThrow(userId);
        Exercise exercise =  getExerciseOrThrow(exerciseId, userDefectType.getDefectType().getId());

//        progressService.validateExerciseAccess(user, exercise);

        ExerciseFullResponse response = new ExerciseFullResponse();
        response.setId(exercise.getId());
        response.setTitle(exercise.getTitle());
        response.setInstruction(exercise.getInstruction());
        response.setExerciseType(exercise.getExerciseType());

        if(exercise.getExerciseType() == ExerciseType.READ_ALOUD){
            response.setExpectedText(exercise.getExpectedText());
            response.setReferenceAudioUrl(null);
        } else if(exercise.getExerciseType() == ExerciseType.REPEAT_AFTER_AUDIO){
            if (exercise.getReferenceAudioUrl() != null) {
                response.setReferenceAudioUrl(minioService.getPresignedUrl(exercise.getReferenceAudioUrl()));
                response.setExpectedText(null);

            }
        }
        return response;
    }

    @Transactional
    public SubmitExerciseResponse submitExercise(UUID userId, UUID exerciseId, MultipartFile multipartFile) {
        try {
            User user = getUserOrThrow(userId);
            UserDefectType userDefectType = getUserDefectTypeOrThrow(userId);
            Exercise exercise = getExerciseOrThrow(exerciseId, userDefectType.getDefectType().getId());

            //        progressService.validateExerciseAccess(user, exercise);

            byte[] audioBytes = extractAudioBytes(multipartFile);
            String objectKey = uploadToStorage(user, exercise, audioBytes);

            UserExercise attempt = createAttempt(user, exercise, objectKey);

            try {
                AnalyzeResponse aiResponse = aiService.analyze(audioBytes, exercise.getExpectedText());

                if (aiResponse == null) {
                    failAttempt(attempt);
                    throw new RuntimeException("AI module returned null");
                }

                saveAnalysisResult(attempt, aiResponse);
                completeAttempt(attempt);

                //            if (aiResponse.getOverallScore()>= PASS_THRESHOLD){
                //                progressService.handleSuccessfulAttempt(user, exercise);
                //            }

                return SubmitExerciseResponse.builder()
                        .attemptId(attempt.getId())
                        .overallScore(aiResponse.getOverallScore())
                        .feedback(aiResponse.getFeedback())
                        .build();

            } catch (Exception e) {
                failAttempt(attempt);
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    private void completeAttempt(UserExercise attempt) {
        attempt.setStatus(ExerciseStatus.COMPLETED);
        attempt.setCompletedAt(LocalDateTime.now());
        userExerciseRepository.save(attempt);
    }

    private void saveAnalysisResult(UserExercise attempt, AnalyzeResponse aiResponse) {
        AiAnalysisResult result = AiAnalysisResult.builder()
                    .userExercise(attempt)
                    .pronunciationScore(aiResponse.getPronunciationScore())
                    .fluencyScore(aiResponse.getFluencyScore())
                    .embeddingScore(aiResponse.getEmbeddingScore())
                    .overallScore(aiResponse.getOverallScore())
                    .feedback(aiResponse.getFeedback())
                    .flags(aiResponse.getFlags())
                    .metrics(aiResponse.getMetrics())
                    .modelVersion("v1")
                    .createdAt(LocalDateTime.now())
                    .build();

            aiAnalysisResultRepository.save(result);
    }

    private void failAttempt(UserExercise attempt) {
        attempt.setStatus(ExerciseStatus.FAILED);
        userExerciseRepository.save(attempt);
    }

    private UserExercise createAttempt(User user, Exercise exercise, String objectKey) {
        int attemptNumber = userExerciseRepository
                .countByUserIdAndExerciseId(user.getId(), exercise.getId()) + 1;

        UserExercise attempt = new UserExercise();
        attempt.setUser(user);
        attempt.setExercise(exercise);
        attempt.setAttemptNumber(attemptNumber);
        attempt.setAudioUrl(objectKey);
        attempt.setStatus(ExerciseStatus.PENDING);

        return userExerciseRepository.save(attempt);
    }

    private String uploadToStorage(User user, Exercise exercise, byte[] audioBytes) {
        String objectKey = user.getId()
                + "/" + exercise.getId()
                + "/" + UUID.randomUUID() + ".wav";

        minioService.putObject(objectKey, audioBytes);
        return objectKey;
    }

    private byte[] extractAudioBytes(MultipartFile multipartFile) {
        try{
            return multipartFile.getBytes();
        } catch (Exception e){
            throw new RuntimeException("Problem reading file");
        }
    }

    private Exercise getExerciseOrThrow(UUID exerciseId, Integer userDefectTypeId) {
        return exerciseRepository
                .findByIdAndUnit_Course_DefectType_Id(exerciseId, userDefectTypeId)
                .orElseThrow(() -> new RuntimeException("Exercise does not belong to user's defect type"));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private UserDefectType getUserDefectTypeOrThrow(UUID userId) {
        return userDefectTypeRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("User defect type not found"));
    }

}
