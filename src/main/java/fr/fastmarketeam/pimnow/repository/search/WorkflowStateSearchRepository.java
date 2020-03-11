package fr.fastmarketeam.pimnow.repository.search;
import fr.fastmarketeam.pimnow.domain.WorkflowState;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link WorkflowState} entity.
 */
public interface WorkflowStateSearchRepository extends ElasticsearchRepository<WorkflowState, Long> {
}
