package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.AttributValue;
import fr.fastmarketeam.pimnow.repository.AttributValueRepository;
import fr.fastmarketeam.pimnow.repository.search.AttributValueSearchRepository;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.AttributValue}.
 */
@RestController
@RequestMapping("/api")
public class AttributValueResource {

    private final Logger log = LoggerFactory.getLogger(AttributValueResource.class);

    private static final String ENTITY_NAME = "attributValue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AttributValueRepository attributValueRepository;

    private final AttributValueSearchRepository attributValueSearchRepository;

    public AttributValueResource(AttributValueRepository attributValueRepository, AttributValueSearchRepository attributValueSearchRepository) {
        this.attributValueRepository = attributValueRepository;
        this.attributValueSearchRepository = attributValueSearchRepository;
    }

    /**
     * {@code POST  /attribut-values} : Create a new attributValue.
     *
     * @param attributValue the attributValue to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new attributValue, or with status {@code 400 (Bad Request)} if the attributValue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/attribut-values")
    public ResponseEntity<AttributValue> createAttributValue(@RequestBody AttributValue attributValue) throws URISyntaxException {
        log.debug("REST request to save AttributValue : {}", attributValue);
        if (attributValue.getId() != null) {
            throw new BadRequestAlertException("A new attributValue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AttributValue result = attributValueRepository.save(attributValue);
        attributValueSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/attribut-values/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /attribut-values} : Updates an existing attributValue.
     *
     * @param attributValue the attributValue to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attributValue,
     * or with status {@code 400 (Bad Request)} if the attributValue is not valid,
     * or with status {@code 500 (Internal Server Error)} if the attributValue couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/attribut-values")
    public ResponseEntity<AttributValue> updateAttributValue(@RequestBody AttributValue attributValue) throws URISyntaxException {
        log.debug("REST request to update AttributValue : {}", attributValue);
        if (attributValue.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AttributValue result = attributValueRepository.save(attributValue);
        attributValueSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, attributValue.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /attribut-values} : get all the attributValues.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of attributValues in body.
     */
    @GetMapping("/attribut-values")
    public ResponseEntity<List<AttributValue>> getAllAttributValues(Pageable pageable) {
        log.debug("REST request to get a page of AttributValues");
        Page<AttributValue> page = attributValueRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /attribut-values/:id} : get the "id" attributValue.
     *
     * @param id the id of the attributValue to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attributValue, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/attribut-values/{id}")
    public ResponseEntity<AttributValue> getAttributValue(@PathVariable Long id) {
        log.debug("REST request to get AttributValue : {}", id);
        Optional<AttributValue> attributValue = attributValueRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(attributValue);
    }

    /**
     * {@code DELETE  /attribut-values/:id} : delete the "id" attributValue.
     *
     * @param id the id of the attributValue to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/attribut-values/{id}")
    public ResponseEntity<Void> deleteAttributValue(@PathVariable Long id) {
        log.debug("REST request to delete AttributValue : {}", id);
        attributValueRepository.deleteById(id);
        attributValueSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/attribut-values?query=:query} : search for the attributValue corresponding
     * to the query.
     *
     * @param query the query of the attributValue search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/attribut-values")
    public ResponseEntity<List<AttributValue>> searchAttributValues(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AttributValues for query {}", query);
        Page<AttributValue> page = attributValueSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
