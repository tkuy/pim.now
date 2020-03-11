package fr.fastmarketeam.pimnow.repository;

import fr.fastmarketeam.pimnow.domain.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Workflow entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

}
