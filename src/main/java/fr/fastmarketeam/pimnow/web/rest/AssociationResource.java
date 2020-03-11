package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.Association;
import fr.fastmarketeam.pimnow.repository.AssociationRepository;
import fr.fastmarketeam.pimnow.repository.search.AssociationSearchRepository;
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
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.Association}.
 */
@RestController
@RequestMapping("/api")
public class AssociationResource {

    private final Logger log = LoggerFactory.getLogger(AssociationResource.class);

    private static final String ENTITY_NAME = "association";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AssociationRepository associationRepository;

    private final AssociationSearchRepository associationSearchRepository;

    public AssociationResource(AssociationRepository associationRepository, AssociationSearchRepository associationSearchRepository) {
        this.associationRepository = associationRepository;
        this.associationSearchRepository = associationSearchRepository;
    }

    /**
     * {@code POST  /associations} : Create a new association.
     *
     * @param association the association to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new association, or with status {@code 400 (Bad Request)} if the association has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/associations")
    public ResponseEntity<Association> createAssociation(@Valid @RequestBody Association association) throws URISyntaxException {
        log.debug("REST request to save Association : {}", association);
        if (association.getId() != null) {
            throw new BadRequestAlertException("A new association cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Association result = associationRepository.save(association);
        associationSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/associations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /associations} : Updates an existing association.
     *
     * @param association the association to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated association,
     * or with status {@code 400 (Bad Request)} if the association is not valid,
     * or with status {@code 500 (Internal Server Error)} if the association couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/associations")
    public ResponseEntity<Association> updateAssociation(@Valid @RequestBody Association association) throws URISyntaxException {
        log.debug("REST request to update Association : {}", association);
        if (association.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Association result = associationRepository.save(association);
        associationSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, association.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /associations} : get all the associations.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of associations in body.
     */
    @GetMapping("/associations")
    public List<Association> getAllAssociations() {
        log.debug("REST request to get all Associations");
        return associationRepository.findAll();
    }

    /**
     * {@code GET  /associations/:id} : get the "id" association.
     *
     * @param id the id of the association to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the association, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/associations/{id}")
    public ResponseEntity<Association> getAssociation(@PathVariable Long id) {
        log.debug("REST request to get Association : {}", id);
        Optional<Association> association = associationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(association);
    }

    /**
     * {@code DELETE  /associations/:id} : delete the "id" association.
     *
     * @param id the id of the association to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/associations/{id}")
    public ResponseEntity<Void> deleteAssociation(@PathVariable Long id) {
        log.debug("REST request to delete Association : {}", id);
        associationRepository.deleteById(id);
        associationSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/associations?query=:query} : search for the association corresponding
     * to the query.
     *
     * @param query the query of the association search.
     * @return the result of the search.
     */
    @GetMapping("/_search/associations")
    public List<Association> searchAssociations(@RequestParam String query) {
        log.debug("REST request to search Associations for query {}", query);
        return StreamSupport
            .stream(associationSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
