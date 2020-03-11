package fr.fastmarketeam.pimnow.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.fastmarketeam.pimnow.domain.enumeration.AttributType;
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
 * A Attribut.
 */
@Entity
@Table(name = "attribut")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "attribut")
public class Attribut implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String idFAttributIdF = "ATTR_IDF";
    public static final String idFAttributNom = "ATTR_NOM";
    public static final String idFAttributDescription = "ATTR_DESCRIPTION";
    public static final String idFAttributFamille = "ATTR_FAMILLE";
    public static final String idFAttributCategorie = "ATTR_CATEGORIE";
    public static final String idFAttributPrix = "ATTR_PRIX";
    public static final String idFAttributStock = "ATTR_STOCK";


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "id_f", nullable = false)
    private String idF;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AttributType type;

    @ManyToOne
    @JsonIgnoreProperties("attributs")
    private Customer customer;

    @ManyToMany(mappedBy = "attributes")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<Family> families = new HashSet<>();

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

    public Attribut idF(String idF) {
        this.idF = idF;
        return this;
    }

    public void setIdF(String idF) {
        this.idF = idF;
    }

    public String getNom() {
        return nom;
    }

    public Attribut nom(String nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public AttributType getType() {
        return type;
    }

    public Attribut type(AttributType type) {
        this.type = type;
        return this;
    }

    public void setType(AttributType type) {
        this.type = type;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Attribut customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Set<Family> getFamilies() {
        return families;
    }

    public Attribut families(Set<Family> families) {
        this.families = families;
        return this;
    }

    public Attribut addFamily(Family family) {
        this.families.add(family);
        family.getAttributes().add(this);
        return this;
    }

    public Attribut removeFamily(Family family) {
        this.families.remove(family);
        family.getAttributes().remove(this);
        return this;
    }

    public void setFamilies(Set<Family> families) {
        this.families = families;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attribut)) {
            return false;
        }
        return id != null && id.equals(((Attribut) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Attribut{" +
            "id=" + getId() +
            ", idF='" + getIdF() + "'" +
            ", nom='" + getNom() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
