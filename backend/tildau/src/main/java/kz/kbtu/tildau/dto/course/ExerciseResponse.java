package kz.kbtu.tildau.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ExerciseResponse {
    private UUID id;
    private String title;
    private String instruction;
}
