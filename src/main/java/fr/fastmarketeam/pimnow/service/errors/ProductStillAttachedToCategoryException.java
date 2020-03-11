package fr.fastmarketeam.pimnow.service.errors;

public class ProductStillAttachedToCategoryException extends RuntimeException {
    public ProductStillAttachedToCategoryException() {
        super("You cannot delete this category, it still has products attached to it");
    }
}
