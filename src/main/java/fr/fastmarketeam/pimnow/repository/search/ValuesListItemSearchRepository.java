package fr.fastmarketeam.pimnow.repository.search;
import fr.fastmarketeam.pimnow.domain.ValuesListItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ValuesListItem} entity.
 */
public interface ValuesListItemSearchRepository extends ElasticsearchRepository<ValuesListItem, Long> {
}
