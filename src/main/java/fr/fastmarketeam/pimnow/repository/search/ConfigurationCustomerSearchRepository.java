package fr.fastmarketeam.pimnow.repository.search;
import fr.fastmarketeam.pimnow.domain.ConfigurationCustomer;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ConfigurationCustomer} entity.
 */
public interface ConfigurationCustomerSearchRepository extends ElasticsearchRepository<ConfigurationCustomer, Long> {
}
