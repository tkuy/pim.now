package fr.fastmarketeam.pimnow.service.errors;

public class RootFamilyException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RootFamilyException() {
        super("This family cannot be deleted, this is the root family");
    }
}
