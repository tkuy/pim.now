package fr.fastmarketeam.pimnow.service.errors;

public class WrongQueryException extends RuntimeException {
    public WrongQueryException(String message) {
        super(message);
    }
}
