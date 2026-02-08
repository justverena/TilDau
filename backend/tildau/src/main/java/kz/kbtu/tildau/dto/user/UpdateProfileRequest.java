package kz.kbtu.tildau.dto.user;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class UpdateProfileRequest {
    String name;
    String email;
    String password;

}
