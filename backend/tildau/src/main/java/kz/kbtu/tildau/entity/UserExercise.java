package kz.kbtu.tildau.entity;

import jakarta.persistence.*;
import kz.kbtu.tildau.enums.ExerciseStatus;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_exercises")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserExercise {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "attempt_number",nullable = false)
    private int attemptNumber;

    @Column(name = "audio_url", nullable = false)
    private String audioUrl;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "exercise_status")
    private ExerciseStatus status;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
