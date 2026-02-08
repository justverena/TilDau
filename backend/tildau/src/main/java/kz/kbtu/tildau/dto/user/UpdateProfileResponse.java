package kz.kbtu.tildau.dto.user;

public class UpdateProfileResponse {
    private String message;

    public UpdateProfileResponse(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
