package fr.fastmarketeam.pimnow.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A PrestashopProduct.
 */
@Entity
@Table(name = "prestashop_product")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "prestashopproduct")
public class PrestashopProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "prestashop_product_id", nullable = false)
    private Long prestashopProductId;

    @OneToOne(optional = false)
    @NotNull
    @MapsId
    @JoinColumn(name = "id")
    private Product productPim;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrestashopProductId() {
        return prestashopProductId;
    }

    public PrestashopProduct prestashopProductId(Long prestashopProductId) {
        this.prestashopProductId = prestashopProductId;
        return this;
    }

    public void setPrestashopProductId(Long prestashopProductId) {
        this.prestashopProductId = prestashopProductId;
    }

    public Product getProductPim() {
        return productPim;
    }

    public PrestashopProduct productPim(Product product) {
        this.productPim = product;
        return this;
    }

    public void setProductPim(Product product) {
        this.productPim = product;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrestashopProduct)) {
            return false;
        }
        return id != null && id.equals(((PrestashopProduct) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "PrestashopProduct{" +
            "id=" + getId() +
            ", prestashopProductId=" + getPrestashopProductId() +
            "}";
    }
}
