package kz.kbtu.tildau.service;

import lombok.RequiredArgsConstructor;
import kz.kbtu.tildau.entity.*;
import kz.kbtu.tildau.enums.ExerciseStatus;
import kz.kbtu.tildau.repository.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ActivityService {

    private final UserExerciseRepository userExerciseRepository;

    public List<UserExercise> getCompletedAttempts(UUID userId) {
        return userExerciseRepository.findAll()
                .stream()
                .filter(a -> a.getUser().getId().equals(userId)
                        && a.getStatus() == ExerciseStatus.COMPLETED)
                .toList();
    }

    public Set<LocalDate> extractActiveDays(List<UserExercise> attempts) {
        return attempts.stream()
                .map(a -> a.getCompletedAt().toLocalDate())
                .collect(Collectors.toSet());
    }

    public int calculateStreak(Set<LocalDate> days) {
        int streak = 0;
        LocalDate today = LocalDate.now();
        while (days.contains(today)) {
            streak++;
            today = today.minusDays(1);
        }
        return streak;
    }
}