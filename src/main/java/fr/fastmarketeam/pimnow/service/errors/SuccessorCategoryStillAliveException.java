package fr.fastmarketeam.pimnow.service.errors;

public class SuccessorCategoryStillAliveException extends RuntimeException {
    public SuccessorCategoryStillAliveException() {
        super("You cannot delete this category, it still has active successors");
    }
}
