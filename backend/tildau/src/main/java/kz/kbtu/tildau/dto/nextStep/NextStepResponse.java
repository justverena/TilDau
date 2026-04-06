package kz.kbtu.tildau.dto.nextStep;

import kz.kbtu.tildau.enums.NextStepType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NextStepResponse {
    private NextStepType type;
    private UUID id;
}