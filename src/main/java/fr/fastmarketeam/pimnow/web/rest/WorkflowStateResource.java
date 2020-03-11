package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.WorkflowState;
import fr.fastmarketeam.pimnow.repository.WorkflowStateRepository;
import fr.fastmarketeam.pimnow.repository.search.WorkflowStateSearchRepository;
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
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.WorkflowState}.
 */
@RestController
@RequestMapping("/api")
public class WorkflowStateResource {

    private final Logger log = LoggerFactory.getLogger(WorkflowStateResource.class);

    private static final String ENTITY_NAME = "workflowState";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkflowStateRepository workflowStateRepository;

    private final WorkflowStateSearchRepository workflowStateSearchRepository;

    public WorkflowStateResource(WorkflowStateRepository workflowStateRepository, WorkflowStateSearchRepository workflowStateSearchRepository) {
        this.workflowStateRepository = workflowStateRepository;
        this.workflowStateSearchRepository = workflowStateSearchRepository;
    }

    /**
     * {@code POST  /workflow-states} : Create a new workflowState.
     *
     * @param workflowState the workflowState to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workflowState, or with status {@code 400 (Bad Request)} if the workflowState has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/workflow-states")
    public ResponseEntity<WorkflowState> createWorkflowState(@Valid @RequestBody WorkflowState workflowState) throws URISyntaxException {
        log.debug("REST request to save WorkflowState : {}", workflowState);
        if (workflowState.getId() != null) {
            throw new BadRequestAlertException("A new workflowState cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WorkflowState result = workflowStateRepository.save(workflowState);
        workflowStateSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/workflow-states/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /workflow-states} : Updates an existing workflowState.
     *
     * @param workflowState the workflowState to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workflowState,
     * or with status {@code 400 (Bad Request)} if the workflowState is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workflowState couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/workflow-states")
    public ResponseEntity<WorkflowState> updateWorkflowState(@Valid @RequestBody WorkflowState workflowState) throws URISyntaxException {
        log.debug("REST request to update WorkflowState : {}", workflowState);
        if (workflowState.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        WorkflowState result = workflowStateRepository.save(workflowState);
        workflowStateSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, workflowState.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /workflow-states} : get all the workflowStates.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workflowStates in body.
     */
    @GetMapping("/workflow-states")
    public List<WorkflowState> getAllWorkflowStates() {
        log.debug("REST request to get all WorkflowStates");
        return workflowStateRepository.findAll();
    }

    /**
     * {@code GET  /workflow-states/:id} : get the "id" workflowState.
     *
     * @param id the id of the workflowState to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workflowState, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/workflow-states/{id}")
    public ResponseEntity<WorkflowState> getWorkflowState(@PathVariable Long id) {
        log.debug("REST request to get WorkflowState : {}", id);
        Optional<WorkflowState> workflowState = workflowStateRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(workflowState);
    }

    /**
     * {@code DELETE  /workflow-states/:id} : delete the "id" workflowState.
     *
     * @param id the id of the workflowState to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/workflow-states/{id}")
    public ResponseEntity<Void> deleteWorkflowState(@PathVariable Long id) {
        log.debug("REST request to delete WorkflowState : {}", id);
        workflowStateRepository.deleteById(id);
        workflowStateSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/workflow-states?query=:query} : search for the workflowState corresponding
     * to the query.
     *
     * @param query the query of the workflowState search.
     * @return the result of the search.
     */
    @GetMapping("/_search/workflow-states")
    public List<WorkflowState> searchWorkflowStates(@RequestParam String query) {
        log.debug("REST request to search WorkflowStates for query {}", query);
        return StreamSupport
            .stream(workflowStateSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
