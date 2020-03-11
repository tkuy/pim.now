package fr.fastmarketeam.pimnow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A AttributValuesList.
 */
@Entity
@Table(name = "attribut_values_list")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "attributvalueslist")
public class AttributValuesList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @OneToOne
    @JoinColumn(unique = true)
    private Attribut attribut;

    @ManyToOne
    @JsonIgnoreProperties("attributValuesLists")
    private ValuesList valuesList;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Attribut getAttribut() {
        return attribut;
    }

    public AttributValuesList attribut(Attribut attribut) {
        this.attribut = attribut;
        return this;
    }

    public void setAttribut(Attribut attribut) {
        this.attribut = attribut;
    }

    public ValuesList getValuesList() {
        return valuesList;
    }

    public AttributValuesList valuesList(ValuesList valuesList) {
        this.valuesList = valuesList;
        return this;
    }

    public void setValuesList(ValuesList valuesList) {
        this.valuesList = valuesList;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttributValuesList)) {
            return false;
        }
        return id != null && id.equals(((AttributValuesList) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "AttributValuesList{" +
            "id=" + getId() +
            "}";
    }
}
