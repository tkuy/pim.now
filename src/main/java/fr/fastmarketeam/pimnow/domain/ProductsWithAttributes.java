package fr.fastmarketeam.pimnow.domain;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Set;

@org.springframework.data.elasticsearch.annotations.Document(indexName = "productwithattributes")
public class ProductsWithAttributes {
    private Long id;
    private Product product;
    @Field( type = FieldType.Nested)
    private Set<AttributValue> attributValues;

    public Product getProduct() {
        return product;
    }
    public Long getId() {
        return this.id;
    }
    public ProductsWithAttributes setProduct(Product product) {
        this.product = product;
        return this;
    }

    public void initId() {
        if (product == null || product.getId() == null) {
            throw new IllegalStateException("Cannot initialize if product is not set or persisted in database");
        }
        this.id = product.getId();
    }

    public Set<AttributValue> getAttributValues() {
        return attributValues;
    }

    public ProductsWithAttributes setAttributValues(Set<AttributValue> attributValues) {
        this.attributValues = attributValues;
        return this;
    }
}
