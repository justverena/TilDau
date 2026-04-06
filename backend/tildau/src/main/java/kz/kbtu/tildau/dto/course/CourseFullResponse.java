package kz.kbtu.tildau.dto.course;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CourseFullResponse {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal progressPercent;
    private List<UnitResponse> units;
}
