package fr.fastmarketeam.pimnow.service.dto;

import fr.fastmarketeam.pimnow.domain.Mapping;

import java.util.HashSet;
import java.util.Set;

public class MappingDuplicateDTO {

    private Mapping mapping;

    private Set<AssociationDuplicateDTO> associations;

    public MappingDuplicateDTO() {
        this.associations = new HashSet<>();
    }

    public MappingDuplicateDTO(Mapping mapping, Set<AssociationDuplicateDTO> associations) {
        this.mapping = mapping;
        this.associations = associations;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    public Set<AssociationDuplicateDTO> getAssociations() {
        return associations;
    }

    public void setAssociations(Set<AssociationDuplicateDTO> associations) {
        this.associations = associations;
    }
}
