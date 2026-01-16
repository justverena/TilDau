package kz.kbtu.tildau.dto;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class UpdateProfileRequest {
    String name;
    String email;
    String password;

}
