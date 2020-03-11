package fr.fastmarketeam.pimnow.web.rest.errors;

public class WrongQueryException extends BadRequestAlertException {
    public WrongQueryException(String message) {
        super(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, message, "product", "wrongQuery");    }
}
