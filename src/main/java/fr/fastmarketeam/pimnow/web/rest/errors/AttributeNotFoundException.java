package fr.fastmarketeam.pimnow.web.rest.errors;

public class AttributeNotFoundException extends BadRequestAlertException {
    public AttributeNotFoundException(String message) {
        super(ErrorConstants.ENTITY_NOT_FOUND_TYPE, message, "attribut", "attributeNotFound");    }
}
