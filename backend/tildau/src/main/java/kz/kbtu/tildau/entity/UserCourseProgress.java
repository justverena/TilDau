package kz.kbtu.tildau.entity;

import jakarta.persistence.*;
import kz.kbtu.tildau.embeddedId.UserCourseProgressId;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_course_progress")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserCourseProgress {
    @EmbeddedId
    private UserCourseProgressId id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "completed_units")
    private int completedUnits;

    @Column(name = "total_units", nullable = false)
    private int totalUnits;

    @Column(name = "progress_percent")
    private BigDecimal progressPercent;

}
