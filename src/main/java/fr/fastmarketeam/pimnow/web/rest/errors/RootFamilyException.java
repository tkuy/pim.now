package fr.fastmarketeam.pimnow.web.rest.errors;

public class RootFamilyException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public RootFamilyException() {
        super(ErrorConstants.DEFAULT_TYPE, "This family cannot be deleted, this is the root family", "family", "familyRootIsNotDeletable");
    }
}
