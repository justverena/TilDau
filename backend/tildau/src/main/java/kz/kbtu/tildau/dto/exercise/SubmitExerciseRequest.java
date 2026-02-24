package kz.kbtu.tildau.dto.exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SubmitExerciseRequest {
    private UUID userId;
    private UUID exerciseId;
    private MultipartFile file;
}
