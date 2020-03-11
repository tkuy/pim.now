package fr.fastmarketeam.pimnow.web.rest.vm;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

public class ImportVM {

    @NotNull
    private Long idMapping;

    @NotNull
    private MultipartFile fileToImport;

    public Long getIdMapping() {
        return idMapping;
    }

    public MultipartFile getFileToImport() {
        return fileToImport;
    }

    public void setIdMapping(Long idMapping) {
        this.idMapping = idMapping;
    }

    public void setFileToImport(MultipartFile fileToImport) {
        this.fileToImport = fileToImport;
    }

    @Override
    public String toString() {
        return "ImportVM{" +
            "idMapping=" + idMapping +
            ", fileToImport=" + fileToImport +
            '}';
    }
}
