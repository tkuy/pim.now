package fr.fastmarketeam.pimnow.repository.search;
import fr.fastmarketeam.pimnow.domain.AttributValuesList;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link AttributValuesList} entity.
 */
public interface AttributValuesListSearchRepository extends ElasticsearchRepository<AttributValuesList, Long> {
}
