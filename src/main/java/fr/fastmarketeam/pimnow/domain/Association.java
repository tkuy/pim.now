package fr.fastmarketeam.pimnow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A Association.
 */
@Entity
@Table(name = "association")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "association")
public class Association implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "jhi_column", nullable = false)
    private String column;

    @NotNull
    @Column(name = "id_f_attribut", nullable = false)
    private String idFAttribut;

    @ManyToOne
    @JsonIgnoreProperties("associations")
    private Mapping mapping;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColumn() {
        return column;
    }

    public Association column(String column) {
        this.column = column;
        return this;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getIdFAttribut() {
        return idFAttribut;
    }

    public Association idFAttribut(String idFAttribut) {
        this.idFAttribut = idFAttribut;
        return this;
    }

    public void setIdFAttribut(String idFAttribut) {
        this.idFAttribut = idFAttribut;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public Association mapping(Mapping mapping) {
        this.mapping = mapping;
        return this;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Association)) {
            return false;
        }
        return id != null && id.equals(((Association) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Association{" +
            "id=" + getId() +
            ", column='" + getColumn() + "'" +
            ", idFAttribut='" + getIdFAttribut() + "'" +
            "}";
    }
}
