package fr.fastmarketeam.pimnow.service.errors;

public class SuccessorFamilyStillAliveException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SuccessorFamilyStillAliveException() {
        super("This family cannot be deleted, a successor is still active");
    }
}
