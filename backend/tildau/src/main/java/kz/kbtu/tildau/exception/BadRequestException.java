package kz.kbtu.tildau.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}