package fr.fastmarketeam.pimnow.repository.search;
import fr.fastmarketeam.pimnow.domain.PrestashopProduct;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link PrestashopProduct} entity.
 */
public interface PrestashopProductSearchRepository extends ElasticsearchRepository<PrestashopProduct, Long> {
}
