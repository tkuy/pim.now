package fr.fastmarketeam.pimnow.service.errors;

import fr.fastmarketeam.pimnow.web.rest.errors.BadRequestAlertException;
import fr.fastmarketeam.pimnow.web.rest.errors.ErrorConstants;

public class MappingNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MappingNotFoundException() {
        super("Mapping not found!");
    }
}
