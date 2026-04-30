package kz.kbtu.tildau.service;

import kz.kbtu.tildau.entity.AiAnalysisResult;
import kz.kbtu.tildau.enums.ExerciseStatus;
import kz.kbtu.tildau.repository.AiAnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AnalysisService {

    private final AiAnalysisResultRepository repository;

    public List<AiAnalysisResult> getSuccessfulResults(UUID userId) {
        return repository.findAll()
                .stream()
                .filter(r -> r.getUserExercise().getUser().getId().equals(userId))
                .filter(r -> r.getUserExercise().getStatus() == ExerciseStatus.COMPLETED)
                .toList();
    }
}