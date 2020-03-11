package fr.fastmarketeam.pimnow.service.errors;

public class UnsupportedFileException extends RuntimeException {
    public UnsupportedFileException() {
        super("The files supported are .txt, .jpeg, .jpg, .xlsx, .pdf");
    }
}
