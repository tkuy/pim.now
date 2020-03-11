package fr.fastmarketeam.pimnow.service.dto;

public class AssociationDuplicateDTO {
    private String column;
    private Long idAttribut;
    private String nameAttribut;

    public AssociationDuplicateDTO() {
    }

    public AssociationDuplicateDTO(String column, Long idAttribut, String nameAttribut) {
        this.column = column;
        this.idAttribut = idAttribut;
        this.nameAttribut = nameAttribut;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Long getIdAttribut() {
        return idAttribut;
    }

    public void setIdAttribut(Long idAttribut) {
        this.idAttribut = idAttribut;
    }

    public String getNameAttribut() {
        return nameAttribut;
    }

    public void setNameAttribut(String nameAttribut) {
        this.nameAttribut = nameAttribut;
    }
}
