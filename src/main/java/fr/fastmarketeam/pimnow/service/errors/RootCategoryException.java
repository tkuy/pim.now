package fr.fastmarketeam.pimnow.service.errors;

public class RootCategoryException extends RuntimeException {
    public RootCategoryException() {
        super("You cannot deleted the category root");
    }
}
