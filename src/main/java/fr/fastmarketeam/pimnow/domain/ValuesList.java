package fr.fastmarketeam.pimnow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A ValuesList.
 */
@Entity
@Table(name = "values_list")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "valueslist")
public class ValuesList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "id_f", nullable = false)
    private String idF;

    @OneToMany(mappedBy = "valuesList")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ValuesListItem> items = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("valuesLists")
    private Customer customer;

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

    public ValuesList idF(String idF) {
        this.idF = idF;
        return this;
    }

    public void setIdF(String idF) {
        this.idF = idF;
    }

    public Set<ValuesListItem> getItems() {
        return items;
    }

    public ValuesList items(Set<ValuesListItem> valuesListItems) {
        this.items = valuesListItems;
        return this;
    }

    public ValuesList addItems(ValuesListItem valuesListItem) {
        this.items.add(valuesListItem);
        valuesListItem.setValuesList(this);
        return this;
    }

    public ValuesList removeItems(ValuesListItem valuesListItem) {
        this.items.remove(valuesListItem);
        valuesListItem.setValuesList(null);
        return this;
    }

    public void setItems(Set<ValuesListItem> valuesListItems) {
        this.items = valuesListItems;
    }

    public Customer getCustomer() {
        return customer;
    }

    public ValuesList customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ValuesList)) {
            return false;
        }
        return id != null && id.equals(((ValuesList) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ValuesList{" +
            "id=" + getId() +
            ", idF='" + getIdF() + "'" +
            "}";
    }
}
