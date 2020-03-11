package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.AttributValuesList;
import fr.fastmarketeam.pimnow.repository.AttributValuesListRepository;
import fr.fastmarketeam.pimnow.repository.search.AttributValuesListSearchRepository;
import fr.fastmarketeam.pimnow.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.AttributValuesList}.
 */
@RestController
@RequestMapping("/api")
public class AttributValuesListResource {

    private final Logger log = LoggerFactory.getLogger(AttributValuesListResource.class);

    private static final String ENTITY_NAME = "attributValuesList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AttributValuesListRepository attributValuesListRepository;

    private final AttributValuesListSearchRepository attributValuesListSearchRepository;

    public AttributValuesListResource(AttributValuesListRepository attributValuesListRepository, AttributValuesListSearchRepository attributValuesListSearchRepository) {
        this.attributValuesListRepository = attributValuesListRepository;
        this.attributValuesListSearchRepository = attributValuesListSearchRepository;
    }

    /**
     * {@code POST  /attribut-values-lists} : Create a new attributValuesList.
     *
     * @param attributValuesList the attributValuesList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new attributValuesList, or with status {@code 400 (Bad Request)} if the attributValuesList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/attribut-values-lists")
    public ResponseEntity<AttributValuesList> createAttributValuesList(@RequestBody AttributValuesList attributValuesList) throws URISyntaxException {
        log.debug("REST request to save AttributValuesList : {}", attributValuesList);
        if (attributValuesList.getId() != null) {
            throw new BadRequestAlertException("A new attributValuesList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AttributValuesList result = attributValuesListRepository.save(attributValuesList);
        attributValuesListSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/attribut-values-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /attribut-values-lists} : Updates an existing attributValuesList.
     *
     * @param attributValuesList the attributValuesList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attributValuesList,
     * or with status {@code 400 (Bad Request)} if the attributValuesList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the attributValuesList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/attribut-values-lists")
    public ResponseEntity<AttributValuesList> updateAttributValuesList(@RequestBody AttributValuesList attributValuesList) throws URISyntaxException {
        log.debug("REST request to update AttributValuesList : {}", attributValuesList);
        if (attributValuesList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AttributValuesList result = attributValuesListRepository.save(attributValuesList);
        attributValuesListSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, attributValuesList.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /attribut-values-lists} : get all the attributValuesLists.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of attributValuesLists in body.
     */
    @GetMapping("/attribut-values-lists")
    public List<AttributValuesList> getAllAttributValuesLists() {
        log.debug("REST request to get all AttributValuesLists");
        return attributValuesListRepository.findAll();
    }

    /**
     * {@code GET  /attribut-values-lists/:id} : get the "id" attributValuesList.
     *
     * @param id the id of the attributValuesList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attributValuesList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/attribut-values-lists/{id}")
    public ResponseEntity<AttributValuesList> getAttributValuesList(@PathVariable Long id) {
        log.debug("REST request to get AttributValuesList : {}", id);
        Optional<AttributValuesList> attributValuesList = attributValuesListRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(attributValuesList);
    }

    /**
     * {@code DELETE  /attribut-values-lists/:id} : delete the "id" attributValuesList.
     *
     * @param id the id of the attributValuesList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/attribut-values-lists/{id}")
    public ResponseEntity<Void> deleteAttributValuesList(@PathVariable Long id) {
        log.debug("REST request to delete AttributValuesList : {}", id);
        attributValuesListRepository.deleteById(id);
        attributValuesListSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/attribut-values-lists?query=:query} : search for the attributValuesList corresponding
     * to the query.
     *
     * @param query the query of the attributValuesList search.
     * @return the result of the search.
     */
    @GetMapping("/_search/attribut-values-lists")
    public List<AttributValuesList> searchAttributValuesLists(@RequestParam String query) {
        log.debug("REST request to search AttributValuesLists for query {}", query);
        return StreamSupport
            .stream(attributValuesListSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
