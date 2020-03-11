package fr.fastmarketeam.pimnow.web.rest.errors;

public class RootCategoryException extends BadRequestAlertException {
    public RootCategoryException() {
        super("You cannot deleted the category root", "category", "categoryRootIsNotDeletable");
    }
}
