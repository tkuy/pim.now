package fr.fastmarketeam.pimnow.service.errors;

public class MissingFileException extends RuntimeException {
    public MissingFileException() {
        super("A name of file is given but no files has been found");
    }
}
