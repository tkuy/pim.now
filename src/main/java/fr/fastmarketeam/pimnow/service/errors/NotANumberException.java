package fr.fastmarketeam.pimnow.service.errors;

public class NotANumberException extends RuntimeException {
    public NotANumberException() {
        super("The value is not a number or cannot be cast to a number");
    }
}
