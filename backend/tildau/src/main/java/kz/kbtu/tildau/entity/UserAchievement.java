package kz.kbtu.tildau.entity;

import jakarta.persistence.*;
import kz.kbtu.tildau.embeddedId.UserAchievementId;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_achievements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievement {
    @EmbeddedId
    private UserAchievementId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("achievementId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;
}
