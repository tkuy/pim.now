package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.Workflow;
import fr.fastmarketeam.pimnow.repository.WorkflowRepository;
import fr.fastmarketeam.pimnow.repository.search.WorkflowSearchRepository;
import fr.fastmarketeam.pimnow.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.Workflow}.
 */
@RestController
@RequestMapping("/api")
public class WorkflowResource {

    private final Logger log = LoggerFactory.getLogger(WorkflowResource.class);

    private static final String ENTITY_NAME = "workflow";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkflowRepository workflowRepository;

    private final WorkflowSearchRepository workflowSearchRepository;

    public WorkflowResource(WorkflowRepository workflowRepository, WorkflowSearchRepository workflowSearchRepository) {
        this.workflowRepository = workflowRepository;
        this.workflowSearchRepository = workflowSearchRepository;
    }

    /**
     * {@code POST  /workflows} : Create a new workflow.
     *
     * @param workflow the workflow to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workflow, or with status {@code 400 (Bad Request)} if the workflow has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/workflows")
    public ResponseEntity<Workflow> createWorkflow(@Valid @RequestBody Workflow workflow) throws URISyntaxException {
        log.debug("REST request to save Workflow : {}", workflow);
        if (workflow.getId() != null) {
            throw new BadRequestAlertException("A new workflow cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Workflow result = workflowRepository.save(workflow);
        workflowSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/workflows/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /workflows} : Updates an existing workflow.
     *
     * @param workflow the workflow to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workflow,
     * or with status {@code 400 (Bad Request)} if the workflow is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workflow couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/workflows")
    public ResponseEntity<Workflow> updateWorkflow(@Valid @RequestBody Workflow workflow) throws URISyntaxException {
        log.debug("REST request to update Workflow : {}", workflow);
        if (workflow.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Workflow result = workflowRepository.save(workflow);
        workflowSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, workflow.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /workflows} : get all the workflows.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workflows in body.
     */
    @GetMapping("/workflows")
    public ResponseEntity<List<Workflow>> getAllWorkflows(Pageable pageable) {
        log.debug("REST request to get a page of Workflows");
        Page<Workflow> page = workflowRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /workflows/:id} : get the "id" workflow.
     *
     * @param id the id of the workflow to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workflow, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/workflows/{id}")
    public ResponseEntity<Workflow> getWorkflow(@PathVariable Long id) {
        log.debug("REST request to get Workflow : {}", id);
        Optional<Workflow> workflow = workflowRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(workflow);
    }

    /**
     * {@code DELETE  /workflows/:id} : delete the "id" workflow.
     *
     * @param id the id of the workflow to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/workflows/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable Long id) {
        log.debug("REST request to delete Workflow : {}", id);
        workflowRepository.deleteById(id);
        workflowSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/workflows?query=:query} : search for the workflow corresponding
     * to the query.
     *
     * @param query the query of the workflow search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/workflows")
    public ResponseEntity<List<Workflow>> searchWorkflows(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Workflows for query {}", query);
        Page<Workflow> page = workflowSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
