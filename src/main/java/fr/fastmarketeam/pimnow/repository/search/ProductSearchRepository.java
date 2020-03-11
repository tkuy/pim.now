package fr.fastmarketeam.pimnow.repository.search;
import fr.fastmarketeam.pimnow.domain.Product;
import fr.fastmarketeam.pimnow.domain.ProductsWithAttributes;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Product} entity.
 */
public interface ProductSearchRepository extends ElasticsearchRepository<ProductsWithAttributes, Long> {
}
