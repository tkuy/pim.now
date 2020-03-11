package fr.fastmarketeam.pimnow.web.rest.errors;

public class CustomerRequiredActionException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public CustomerRequiredActionException() {
        super(ErrorConstants.CONSTRAINT_VIOLATION_TYPE, "A customer is required for this action", "family", "customerrequired");
    }
}
