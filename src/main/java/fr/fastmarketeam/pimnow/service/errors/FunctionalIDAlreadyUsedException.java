package fr.fastmarketeam.pimnow.service.errors;

public class FunctionalIDAlreadyUsedException extends RuntimeException {
    public FunctionalIDAlreadyUsedException() {
        super("The functional ID is already used");
    }
}
