package kz.kbtu.tildau.service;

import kz.kbtu.tildau.dto.exercise.ExerciseFullResponse;
import kz.kbtu.tildau.entity.Exercise;
import kz.kbtu.tildau.entity.User;
import kz.kbtu.tildau.entity.UserDefectType;
import kz.kbtu.tildau.enums.ExerciseType;
import kz.kbtu.tildau.exception.NotFoundException;
import kz.kbtu.tildau.repository.ExerciseRepository;
import kz.kbtu.tildau.repository.UserDefectTypeRepository;
import kz.kbtu.tildau.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ExerciseService {
    private final UserJpaRepository userRepository;
    private final UserDefectTypeRepository defectTypeRepository;
    private final ExerciseRepository exerciseRepository;
    private final MinioService minioService;
    private final UserDefectTypeRepository userDefectTypeRepository;

    public ExerciseFullResponse getExercise(UUID userId, UUID exerciseId) {
        User user  = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        UserDefectType userDefectType = userDefectTypeRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("User defect type not found"));

        Exercise exercise = exerciseRepository
                .findByIdAndUnit_Course_DefectType_Id(exerciseId, userDefectType.getDefectType().getId())
                .orElseThrow(() -> new RuntimeException("Exercise does not belong to user's defect type"));

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
}
