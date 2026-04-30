package kz.kbtu.tildau.dto.stats;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillTrendResponse {
    private double pronunciation;
    private double fluency;
    private double overall;
}