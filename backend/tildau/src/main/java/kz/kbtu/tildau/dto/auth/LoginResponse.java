package kz.kbtu.tildau.dto.auth;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class LoginResponse {
    private String token;

    public LoginResponse() {}

    public LoginResponse(String token) {
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }
}