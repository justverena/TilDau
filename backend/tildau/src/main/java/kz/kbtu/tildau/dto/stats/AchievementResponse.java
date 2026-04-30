package kz.kbtu.tildau.dto.stats;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AchievementResponse {
    private String code;
    private String title;
    private String description;
    private LocalDateTime unlockedAt;
}