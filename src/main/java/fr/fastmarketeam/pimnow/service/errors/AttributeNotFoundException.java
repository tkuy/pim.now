package fr.fastmarketeam.pimnow.service.errors;

public class AttributeNotFoundException extends RuntimeException {
    public AttributeNotFoundException(String attribute) {
        super("The attribute " + attribute + " was not found");
    }
}
