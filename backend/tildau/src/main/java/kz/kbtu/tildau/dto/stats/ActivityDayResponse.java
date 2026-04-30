package kz.kbtu.tildau.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ActivityDayResponse {
    private LocalDate date;
    private int exercisesCompleted;
}