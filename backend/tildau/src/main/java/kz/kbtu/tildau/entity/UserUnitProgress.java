package kz.kbtu.tildau.entity;

import jakarta.persistence.*;
import kz.kbtu.tildau.embeddedId.UserUnitProgressId;
import lombok.*;


@Entity
@Table(name = "user_unit_progress")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserUnitProgress {

    @EmbeddedId
    private UserUnitProgressId id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("unitId")
    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Column(name = "completed_exercises")
    private int completedExercises;

    @Column(name = "total_exercises", nullable = false)
    private int totalExercises;

    @Column(name = "is_completed")
    private boolean isCompleted;

}
