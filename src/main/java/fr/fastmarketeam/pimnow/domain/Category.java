package fr.fastmarketeam.pimnow.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * A Category.
 */
@Entity
@Table(name = "category")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "category")
public class Category implements Serializable {

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
    private Category predecessor;

    @ManyToOne
    @JsonIgnoreProperties("categories")
    private Customer customer;

    @OneToMany(mappedBy = "predecessor", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Category> successors = new HashSet<>();

    @ManyToMany(mappedBy = "categories")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<Product> products = new HashSet<>();

    @Column(name = "deleted")
    private Boolean deleted = false;

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

    public Category idF(String idF) {
        this.idF = idF;
        return this;
    }

    public void setIdF(String idF) {
        this.idF = idF;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public Category deleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }


    public String getNom() {
        return nom;
    }

    public Category nom(String nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Category getPredecessor() {
        return predecessor;
    }

    public Category predecessor(Category category) {
        this.predecessor = category;
        return this;
    }

    public void setPredecessor(Category category) {
        this.predecessor = category;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Category customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Set<Category> getSuccessors() {
        return successors;
    }

    public Category successors(Set<Category> categories) {
        this.successors = categories;
        return this;
    }

    public Category addSuccessors(Category category) {
        this.successors.add(category);
        category.setPredecessor(this);
        return this;
    }

    public Category removeSuccessors(Category category) {
        this.successors.remove(category);
        category.setPredecessor(null);
        return this;
    }

    public void setSuccessors(Set<Category> categories) {
        this.successors = categories;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public Category products(Set<Product> products) {
        this.products = products;
        return this;
    }

    public Category addProduct(Product product) {
        this.products.add(product);
        product.getCategories().add(this);
        return this;
    }

    public Category removeProduct(Product product) {
        this.products.remove(product);
        product.getCategories().remove(this);
        return this;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return id != null && id.equals(((Category) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Category{" +
            "id=" + getId() +
            ", idF='" + getIdF() + "'" +
            ", nom='" + getNom() + "'" +
             ", deleted='" + isDeleted() + "'" +
           "}";
    }
}
