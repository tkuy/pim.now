package fr.fastmarketeam.pimnow.repository.search;
import fr.fastmarketeam.pimnow.domain.AttributValue;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link AttributValue} entity.
 */
public interface AttributValueSearchRepository extends ElasticsearchRepository<AttributValue, Long> {
}
