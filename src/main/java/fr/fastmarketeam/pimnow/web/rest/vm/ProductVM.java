package fr.fastmarketeam.pimnow.web.rest.vm;

import fr.fastmarketeam.pimnow.domain.*;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Product.
 */
public class ProductVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Size(max = 50)
    private String idF;

    private String nom;

    private String description;

    private Family family;

    private Set<Category> categories = new HashSet<>();

    private Set<Workflow> workflows = new HashSet<>();

    private Set<AttributValue> attributValues = new HashSet<>();

    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdF() {
        return idF;
    }

    public void setIdF(String idF) {
        this.idF = idF;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<Workflow> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(Set<Workflow> workflows) {
        this.workflows = workflows;
    }

    public Set<AttributValue> getAttributValues() {
        return attributValues;
    }

    public void setAttributValues(Set<AttributValue> attributValues) {
        this.attributValues = attributValues;
    }

    public Product toProduct() {
        Product product = new Product();
        product.setIdF(idF);
        product.setNom(nom);
        product.setDescription(description);
        product.setId(id);
        return product;
    }

    public ProductVM toProductVM(Product product) {
        setId(product.getId());
        setIdF(product.getIdF());
        setNom(product.getNom());
        setDescription(product.getDescription());
        setFamily(product.getFamily());
        setCustomer(product.getCustomer());
        setCategories(product.getCategories());
        return this;
    }
}
