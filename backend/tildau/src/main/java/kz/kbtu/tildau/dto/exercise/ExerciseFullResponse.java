package kz.kbtu.tildau.dto.exercise;

import kz.kbtu.tildau.enums.ExerciseType;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ExerciseFullResponse {
    private UUID id;
    private String title;
    private String instruction;
    private ExerciseType exerciseType;
    private String expectedText;
    private String referenceAudioUrl;


}
 