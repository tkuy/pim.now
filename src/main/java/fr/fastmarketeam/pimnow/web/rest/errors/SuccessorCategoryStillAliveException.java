package fr.fastmarketeam.pimnow.web.rest.errors;

public class SuccessorCategoryStillAliveException extends BadRequestAlertException {
    public SuccessorCategoryStillAliveException() {
        super("You cannot deleted this category, it still has successors", "category", "categorySuccessorStillActive");
    }
}
