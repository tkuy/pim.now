package fr.fastmarketeam.pimnow.repository.search;
import fr.fastmarketeam.pimnow.domain.ValuesList;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ValuesList} entity.
 */
public interface ValuesListSearchRepository extends ElasticsearchRepository<ValuesList, Long> {
}
