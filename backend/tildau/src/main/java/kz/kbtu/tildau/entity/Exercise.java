package kz.kbtu.tildau.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "exercises")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Exercise {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private CourseUnit unit;

    @Column(name = "exercise_type")
    private String exerciseType;

    @Column(name = "title")
    private String title;

    @Column(name = "instruction")
    private String instruction;

    @Column(name = "expected_text", nullable = false)
    private String expectedText;

    @Column(name = "reference_audio_url")
    private String referenceAudioUrl;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
