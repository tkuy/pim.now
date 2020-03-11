package fr.fastmarketeam.pimnow.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

/**
 * A ConfigurationCustomer.
 */
@Entity
@Table(name = "configuration_customer")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "configurationcustomer")
public class ConfigurationCustomer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "url_prestashop", nullable = false)
    private String urlPrestashop;

    @NotNull
    @Column(name = "api_key_prestashop", nullable = false)
    private String apiKeyPrestashop;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Customer customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrlPrestashop() {
        return urlPrestashop;
    }

    public ConfigurationCustomer urlPrestashop(String urlPrestashop) {
        this.urlPrestashop = urlPrestashop;
        return this;
    }

    public void setUrlPrestashop(String urlPrestashop) {
        this.urlPrestashop = urlPrestashop;
    }

    public String getApiKeyPrestashop() {
        return apiKeyPrestashop;
    }

    public ConfigurationCustomer apiKeyPrestashop(String apiKeyPrestashop) {
        this.apiKeyPrestashop = apiKeyPrestashop;
        return this;
    }

    public void setApiKeyPrestashop(String apiKeyPrestashop) {
        this.apiKeyPrestashop = apiKeyPrestashop;
    }

    public Customer getCustomer() {
        return customer;
    }

    public ConfigurationCustomer customer(Customer customer) {
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
        if (!(o instanceof ConfigurationCustomer)) {
            return false;
        }
        return id != null && id.equals(((ConfigurationCustomer) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ConfigurationCustomer{" +
            "id=" + getId() +
            ", urlPrestashop='" + getUrlPrestashop() + "'" +
            ", apiKeyPrestashop='" + getApiKeyPrestashop() + "'" +
            "}";
    }
}
