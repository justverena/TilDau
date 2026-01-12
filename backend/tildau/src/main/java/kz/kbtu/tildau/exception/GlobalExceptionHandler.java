package kz.kbtu.tildau.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage();

        if (msg.contains("All fields are required") ||
                msg.contains("Invalid email format") ||
                msg.contains("User with this email already exists")) {
            return ResponseEntity.badRequest().body(new ErrorResponse(msg));
        }

        if (msg.contains("User not found") || msg.contains("Invalid password")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(msg));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(msg));
    }

    static record ErrorResponse(String message) {}
}