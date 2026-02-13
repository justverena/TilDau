package kz.kbtu.tildau.dto.exercise;

import kz.kbtu.tildau.enums.ExerciseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ExerciseFullResponse {
    private UUID id;
    private String title;
    private String instruction;
    private ExerciseType exerciseType;
    private String expectedText;
    private String referenceAudioUrl;
}
