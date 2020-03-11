package fr.fastmarketeam.pimnow.service.errors;

public class ProductStillAttachedToFamilyException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProductStillAttachedToFamilyException() {
        super("This family cannot be deleted, some products are belonging to it");
    }
}
