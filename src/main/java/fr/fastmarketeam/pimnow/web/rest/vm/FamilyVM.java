package fr.fastmarketeam.pimnow.web.rest.vm;

import fr.fastmarketeam.pimnow.domain.Attribut;
import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Family;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

public class FamilyVM {
    private Long id;

    @Size(max = 50)
    private String idF;

    private String nom;

    private Family family;

    public Long getIdPredecessor() {
        return idPredecessor;
    }

    public void setIdPredecessor(Long idPredecessor) {
        this.idPredecessor = idPredecessor;
    }

    private Long idPredecessor;

    private Set<Family> successors = new HashSet<>();

    private Family predecessor;

    private Customer customer;

    private Set<@Valid Attribut> attributes = new HashSet<>();

    private Set<Attribut> newAttributes = new HashSet<>();

    private Set<Attribut> newExistingAttributes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public Set<Attribut> getNewExistingAttributes() {
        return newExistingAttributes;
    }

    public void setNewExistingAttributes(Set<Attribut> newExistingAttributes) {
        this.newExistingAttributes = newExistingAttributes;
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

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public Set<Family> getSuccessors() {
        return successors;
    }

    public void setSuccessors(Set<Family> successors) {
        this.successors = successors;
    }

    public Family getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Family predecessor) {
        this.predecessor = predecessor;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Set<Attribut> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<Attribut> attributes) {
        this.attributes = attributes;
    }

    public Set<Attribut> getNewAttributes() {
        return newAttributes;
    }

    public void setNewAttributes(Set<Attribut> newAttributes) {
        this.newAttributes = newAttributes;
    }

    public Family toFamily() {
        return new Family(id, idF, nom, this.family, successors, predecessor, customer, attributes);
    }
}
