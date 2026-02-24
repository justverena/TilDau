package kz.kbtu.tildau.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "ai_analysis_results")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AiAnalysisResult {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_exercise_id", nullable = false, unique = true)
    private UserExercise userExercise;

    @Column(name = "pronunciation_score")
    private int pronunciationScore;

    @Column(name = "fluency_score")
    private int fluencyScore;

    @Column(name = "embedding_score")
    private int embeddingScore;

    @Column(name = "overall_score")
    private int overallScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "flags", columnDefinition = "jsonb")
    private List<String> flags;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "feedback", columnDefinition = "jsonb")
    private List<String> feedback;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metrics", columnDefinition = "jsonb")
    private Map<String, Object> metrics;

    @Column(name = "model_version")
    private String modelVersion;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
