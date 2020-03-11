package fr.fastmarketeam.pimnow.web.rest.errors;

public class ProductStillAttachedToFamilyException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public ProductStillAttachedToFamilyException() {
        super(ErrorConstants.DEFAULT_TYPE, "This family cannot be deleted, some products are belonging to it", "family", "productStillAttached");
    }
}
