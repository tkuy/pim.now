package fr.fastmarketeam.pimnow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Mapping.
 */
@Entity
@Table(name = "mapping")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mapping")
public class Mapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "id_f", nullable = false)
    private String idF;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "separator")
    private String separator;

    @ManyToOne
    @JsonIgnoreProperties("mappings")
    private Customer customer;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "mapping")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Association> associations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdF() {
        return idF;
    }

    public Mapping idF(String idF) {
        this.idF = idF;
        return this;
    }

    public void setIdF(String idF) {
        this.idF = idF;
    }

    public String getName() {
        return name;
    }

    public Mapping name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Mapping description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeparator() {
        return separator;
    }

    public Mapping separator(String separator) {
        this.separator = separator;
        return this;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Mapping customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Set<Association> getAssociations() {
        return associations;
    }

    public Mapping associations(Set<Association> associations) {
        this.associations = associations;
        return this;
    }

    public Mapping addAssociations(Association association) {
        this.associations.add(association);
        association.setMapping(this);
        return this;
    }

    public Mapping removeAssociations(Association association) {
        this.associations.remove(association);
        association.setMapping(null);
        return this;
    }

    public void setAssociations(Set<Association> associations) {
        this.associations = associations;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Mapping)) {
            return false;
        }
        return id != null && id.equals(((Mapping) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Mapping{" +
            "id=" + getId() +
            ", idF='" + getIdF() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", separator='" + getSeparator() + "'" +
            "}";
    }
}
