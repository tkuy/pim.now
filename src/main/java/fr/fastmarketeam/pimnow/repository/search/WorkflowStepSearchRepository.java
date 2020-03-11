package fr.fastmarketeam.pimnow.repository.search;
import fr.fastmarketeam.pimnow.domain.WorkflowStep;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link WorkflowStep} entity.
 */
public interface WorkflowStepSearchRepository extends ElasticsearchRepository<WorkflowStep, Long> {
}
