package fr.fastmarketeam.pimnow.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
 * A Family.
 */
@Entity
@Table(name = "family")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "family")
public class Family implements Serializable {

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
    @Column(name = "nom", nullable = false)
    private String nom;

    @ManyToOne
    @JsonBackReference
    private Family family;

    @OneToMany(mappedBy = "family", fetch = FetchType.EAGER)
    @JsonManagedReference
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Family> successors = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("families")
    private Family predecessor;

    @ManyToOne
    @JsonIgnoreProperties("families")
    private Customer customer;

    @Column(name = "deleted")
    private Boolean deleted =false;

    @ManyToMany(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "family_attribute",
               joinColumns = @JoinColumn(name = "family_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "attribute_id", referencedColumnName = "id"))
    private Set<Attribut> attributes = new HashSet<>();


    public Family() {
    }

    public Family(@NotNull Long id, @NotNull String idF, @NotNull String nom, Family family, Set<Family> successors, Family predecessor, Customer customer, Set<Attribut> attributes) {
        this.id = id;
        this.idF = idF;
        this.nom = nom;
        this.family = family;
        this.successors = successors;
        this.predecessor = predecessor;
        this.customer = customer;
        this.attributes = attributes;
    }

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

    public Family idF(String idF) {
        this.idF = idF;
        return this;
    }

    public void setIdF(String idF) {
        this.idF = idF;
    }


    public Boolean isDeleted() {
        return deleted;
    }

    public Family deleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }



    public String getNom() {
        return nom;
    }

    public Family nom(String nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Family getFamily() {
        return family;
    }

    public Family family(Family family) {
        this.family = family;
        return this;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public Set<Family> getSuccessors() {
        return successors;
    }

    public Family successors(Set<Family> families) {
        this.successors = families;
        return this;
    }

    public Family addSuccessors(Family family) {
        this.successors.add(family);
        family.setFamily(this);
        return this;
    }

    public Family removeSuccessors(Family family) {
        this.successors.remove(family);
        family.setFamily(null);
        return this;
    }

    public void setSuccessors(Set<Family> families) {
        this.successors = families;
    }

    public Family getPredecessor() {
        return predecessor;
    }

    public Family predecessor(Family family) {
        this.predecessor = family;
        return this;
    }

    public void setPredecessor(Family family) {
        this.predecessor = family;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Family customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Set<Attribut> getAttributes() {
        return attributes;
    }

    public Family attributes(Set<Attribut> attributs) {
        this.attributes = attributs;
        return this;
    }

    public Family addAttribute(Attribut attribut) {
        this.attributes.add(attribut);
        attribut.getFamilies().add(this);
        return this;
    }

    public Family removeAttribute(Attribut attribut) {
        this.attributes.remove(attribut);
        attribut.getFamilies().remove(this);
        return this;
    }

    public void setAttributes(Set<Attribut> attributs) {
        this.attributes = attributs;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Family)) {
            return false;
        }
        return id != null && id.equals(((Family) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Family{" +
            "id=" + getId() +
            ", idF='" + getIdF() + "'" +
            ", nom='" + getNom() + "'" +
            ", deleted='" + isDeleted() + "'" +
            "}";
    }

}
