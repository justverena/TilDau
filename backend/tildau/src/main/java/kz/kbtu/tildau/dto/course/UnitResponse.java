package kz.kbtu.tildau.dto.course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UnitResponse {
    private UUID id;
    private String title;
    private String description;
    private List<ExerciseResponse> exercises;
}
