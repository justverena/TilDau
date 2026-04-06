package kz.kbtu.tildau.entity;

import jakarta.persistence.*;
import kz.kbtu.tildau.embeddedId.UserCourseProgressId;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("courseId")
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "completed_units")
    private int completedUnits;

    @Column(name = "total_units", nullable = false)
    private int totalUnits;

    @Column(name = "progress_percent")
    private BigDecimal progressPercent;

    @CreationTimestamp
    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

}
