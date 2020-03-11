package fr.fastmarketeam.pimnow.repository;

import fr.fastmarketeam.pimnow.domain.WorkflowState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the WorkflowState entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkflowStateRepository extends JpaRepository<WorkflowState, Long> {

}
