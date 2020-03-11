package fr.fastmarketeam.pimnow.web.rest.errors;

public class SuccessorFamilyStillAliveException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public SuccessorFamilyStillAliveException() {
        super(ErrorConstants.DEFAULT_TYPE, "This family cannot be deleted, a successor is still active", "family", "successorStillActive");
    }
}
