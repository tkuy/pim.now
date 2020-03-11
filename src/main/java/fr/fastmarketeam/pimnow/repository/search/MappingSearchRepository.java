package fr.fastmarketeam.pimnow.repository.search;
import fr.fastmarketeam.pimnow.domain.Mapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Mapping} entity.
 */
public interface MappingSearchRepository extends ElasticsearchRepository<Mapping, Long> {
}
