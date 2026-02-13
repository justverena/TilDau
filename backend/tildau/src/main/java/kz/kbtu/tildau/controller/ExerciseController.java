package kz.kbtu.tildau.controller;

import kz.kbtu.tildau.dto.exercise.ExerciseFullResponse;
import kz.kbtu.tildau.security.CustomerUserDetails;
import kz.kbtu.tildau.service.ExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseFullResponse> getExercise(
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            @PathVariable("id") UUID exerciseId
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = userDetails.getUser().getId();

        try {
            ExerciseFullResponse exercise = exerciseService.getExercise(userId, exerciseId);
            return ResponseEntity.ok(exercise);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}