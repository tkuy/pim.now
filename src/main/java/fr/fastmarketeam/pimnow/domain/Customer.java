package fr.fastmarketeam.pimnow.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

/**
 * A Customer.
 */
@Entity
@Table(name = "customer")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "customer")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "id_f", nullable = false)
    private String idF;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "family_root")
    private Integer familyRoot;

    @Column(name = "category_root")
    private Integer categoryRoot;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @OneToOne
    @JoinColumn(unique = true)
    private ConfigurationCustomer configuration;

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

    public Customer idF(String idF) {
        this.idF = idF;
        return this;
    }

    public void setIdF(String idF) {
        this.idF = idF;
    }

    public String getName() {
        return name;
    }

    public Customer name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Customer description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFamilyRoot() {
        return familyRoot;
    }

    public Customer familyRoot(Integer familyRoot) {
        this.familyRoot = familyRoot;
        return this;
    }

    public void setFamilyRoot(Integer familyRoot) {
        this.familyRoot = familyRoot;
    }

    public Integer getCategoryRoot() {
        return categoryRoot;
    }

    public Customer categoryRoot(Integer categoryRoot) {
        this.categoryRoot = categoryRoot;
        return this;
    }

    public void setCategoryRoot(Integer categoryRoot) {
        this.categoryRoot = categoryRoot;
    }

    public Boolean isIsDeleted() {
        return isDeleted;
    }

    public Customer isDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public ConfigurationCustomer getConfiguration() {
        return configuration;
    }

    public Customer configuration(ConfigurationCustomer configurationCustomer) {
        this.configuration = configurationCustomer;
        return this;
    }

    public void setConfiguration(ConfigurationCustomer configurationCustomer) {
        this.configuration = configurationCustomer;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return id != null && id.equals(((Customer) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", idF='" + getIdF() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", familyRoot=" + getFamilyRoot() +
            ", categoryRoot=" + getCategoryRoot() +
            ", isDeleted='" + isIsDeleted() + "'" +
            "}";
    }
}
