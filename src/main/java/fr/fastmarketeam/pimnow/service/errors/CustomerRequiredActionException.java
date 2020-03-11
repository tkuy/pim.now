package fr.fastmarketeam.pimnow.service.errors;

public class CustomerRequiredActionException extends RuntimeException {
    public CustomerRequiredActionException() {
        super("A customer is required for this action");
    }
}
