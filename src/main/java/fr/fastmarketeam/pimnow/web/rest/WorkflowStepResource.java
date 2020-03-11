package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.WorkflowStep;
import fr.fastmarketeam.pimnow.repository.WorkflowStepRepository;
import fr.fastmarketeam.pimnow.repository.search.WorkflowStepSearchRepository;
import fr.fastmarketeam.pimnow.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.WorkflowStep}.
 */
@RestController
@RequestMapping("/api")
public class WorkflowStepResource {

    private final Logger log = LoggerFactory.getLogger(WorkflowStepResource.class);

    private static final String ENTITY_NAME = "workflowStep";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkflowStepRepository workflowStepRepository;

    private final WorkflowStepSearchRepository workflowStepSearchRepository;

    public WorkflowStepResource(WorkflowStepRepository workflowStepRepository, WorkflowStepSearchRepository workflowStepSearchRepository) {
        this.workflowStepRepository = workflowStepRepository;
        this.workflowStepSearchRepository = workflowStepSearchRepository;
    }

    /**
     * {@code POST  /workflow-steps} : Create a new workflowStep.
     *
     * @param workflowStep the workflowStep to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workflowStep, or with status {@code 400 (Bad Request)} if the workflowStep has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/workflow-steps")
    public ResponseEntity<WorkflowStep> createWorkflowStep(@Valid @RequestBody WorkflowStep workflowStep) throws URISyntaxException {
        log.debug("REST request to save WorkflowStep : {}", workflowStep);
        if (workflowStep.getId() != null) {
            throw new BadRequestAlertException("A new workflowStep cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WorkflowStep result = workflowStepRepository.save(workflowStep);
        workflowStepSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/workflow-steps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /workflow-steps} : Updates an existing workflowStep.
     *
     * @param workflowStep the workflowStep to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workflowStep,
     * or with status {@code 400 (Bad Request)} if the workflowStep is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workflowStep couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/workflow-steps")
    public ResponseEntity<WorkflowStep> updateWorkflowStep(@Valid @RequestBody WorkflowStep workflowStep) throws URISyntaxException {
        log.debug("REST request to update WorkflowStep : {}", workflowStep);
        if (workflowStep.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        WorkflowStep result = workflowStepRepository.save(workflowStep);
        workflowStepSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, workflowStep.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /workflow-steps} : get all the workflowSteps.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workflowSteps in body.
     */
    @GetMapping("/workflow-steps")
    public List<WorkflowStep> getAllWorkflowSteps() {
        log.debug("REST request to get all WorkflowSteps");
        return workflowStepRepository.findAll();
    }

    /**
     * {@code GET  /workflow-steps/:id} : get the "id" workflowStep.
     *
     * @param id the id of the workflowStep to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workflowStep, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/workflow-steps/{id}")
    public ResponseEntity<WorkflowStep> getWorkflowStep(@PathVariable Long id) {
        log.debug("REST request to get WorkflowStep : {}", id);
        Optional<WorkflowStep> workflowStep = workflowStepRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(workflowStep);
    }

    /**
     * {@code DELETE  /workflow-steps/:id} : delete the "id" workflowStep.
     *
     * @param id the id of the workflowStep to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/workflow-steps/{id}")
    public ResponseEntity<Void> deleteWorkflowStep(@PathVariable Long id) {
        log.debug("REST request to delete WorkflowStep : {}", id);
        workflowStepRepository.deleteById(id);
        workflowStepSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/workflow-steps?query=:query} : search for the workflowStep corresponding
     * to the query.
     *
     * @param query the query of the workflowStep search.
     * @return the result of the search.
     */
    @GetMapping("/_search/workflow-steps")
    public List<WorkflowStep> searchWorkflowSteps(@RequestParam String query) {
        log.debug("REST request to search WorkflowSteps for query {}", query);
        return StreamSupport
            .stream(workflowStepSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
