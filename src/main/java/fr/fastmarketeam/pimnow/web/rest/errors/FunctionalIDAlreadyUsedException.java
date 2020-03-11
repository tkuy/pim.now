package fr.fastmarketeam.pimnow.web.rest.errors;

public class FunctionalIDAlreadyUsedException extends BadRequestAlertException{
    private static final long serialVersionUID = 1L;

    public FunctionalIDAlreadyUsedException() {
        super(ErrorConstants.DEFAULT_TYPE, "Functional is already used!", "family", "functionalidexists");
    }
}
