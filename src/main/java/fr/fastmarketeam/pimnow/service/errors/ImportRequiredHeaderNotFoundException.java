package fr.fastmarketeam.pimnow.service.errors;

public class ImportRequiredHeaderNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ImportRequiredHeaderNotFoundException(String headersNotFound) {
        super("Required header for import not found ! (" + headersNotFound + ")");
    }
}
