package fr.fastmarketeam.pimnow.repository.search;
import fr.fastmarketeam.pimnow.domain.Attribut;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Attribut} entity.
 */
public interface AttributSearchRepository extends ElasticsearchRepository<Attribut, Long> {
}
