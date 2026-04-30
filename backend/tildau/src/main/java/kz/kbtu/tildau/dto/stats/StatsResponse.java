package kz.kbtu.tildau.dto.stats;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class StatsResponse {
    private int currentStreak;
}