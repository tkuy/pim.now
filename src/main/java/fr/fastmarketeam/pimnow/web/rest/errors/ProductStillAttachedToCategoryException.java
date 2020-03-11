package fr.fastmarketeam.pimnow.web.rest.errors;

public class ProductStillAttachedToCategoryException extends BadRequestAlertException {
    public ProductStillAttachedToCategoryException() {
        super("This category cannot be deleted, some products are belonging to it", "category", "productStillAttachedToCategory");
    }
}
