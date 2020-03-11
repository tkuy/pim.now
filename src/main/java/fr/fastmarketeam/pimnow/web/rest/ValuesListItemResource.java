package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.ValuesListItem;
import fr.fastmarketeam.pimnow.repository.ValuesListItemRepository;
import fr.fastmarketeam.pimnow.repository.search.ValuesListItemSearchRepository;
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
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.ValuesListItem}.
 */
@RestController
@RequestMapping("/api")
public class ValuesListItemResource {

    private final Logger log = LoggerFactory.getLogger(ValuesListItemResource.class);

    private static final String ENTITY_NAME = "valuesListItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ValuesListItemRepository valuesListItemRepository;

    private final ValuesListItemSearchRepository valuesListItemSearchRepository;

    public ValuesListItemResource(ValuesListItemRepository valuesListItemRepository, ValuesListItemSearchRepository valuesListItemSearchRepository) {
        this.valuesListItemRepository = valuesListItemRepository;
        this.valuesListItemSearchRepository = valuesListItemSearchRepository;
    }

    /**
     * {@code POST  /values-list-items} : Create a new valuesListItem.
     *
     * @param valuesListItem the valuesListItem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new valuesListItem, or with status {@code 400 (Bad Request)} if the valuesListItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/values-list-items")
    public ResponseEntity<ValuesListItem> createValuesListItem(@Valid @RequestBody ValuesListItem valuesListItem) throws URISyntaxException {
        log.debug("REST request to save ValuesListItem : {}", valuesListItem);
        if (valuesListItem.getId() != null) {
            throw new BadRequestAlertException("A new valuesListItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ValuesListItem result = valuesListItemRepository.save(valuesListItem);
        valuesListItemSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/values-list-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /values-list-items} : Updates an existing valuesListItem.
     *
     * @param valuesListItem the valuesListItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated valuesListItem,
     * or with status {@code 400 (Bad Request)} if the valuesListItem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the valuesListItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/values-list-items")
    public ResponseEntity<ValuesListItem> updateValuesListItem(@Valid @RequestBody ValuesListItem valuesListItem) throws URISyntaxException {
        log.debug("REST request to update ValuesListItem : {}", valuesListItem);
        if (valuesListItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ValuesListItem result = valuesListItemRepository.save(valuesListItem);
        valuesListItemSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, valuesListItem.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /values-list-items} : get all the valuesListItems.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of valuesListItems in body.
     */
    @GetMapping("/values-list-items")
    public List<ValuesListItem> getAllValuesListItems() {
        log.debug("REST request to get all ValuesListItems");
        return valuesListItemRepository.findAll();
    }

    /**
     * {@code GET  /values-list-items/:id} : get the "id" valuesListItem.
     *
     * @param id the id of the valuesListItem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the valuesListItem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/values-list-items/{id}")
    public ResponseEntity<ValuesListItem> getValuesListItem(@PathVariable Long id) {
        log.debug("REST request to get ValuesListItem : {}", id);
        Optional<ValuesListItem> valuesListItem = valuesListItemRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(valuesListItem);
    }

    /**
     * {@code DELETE  /values-list-items/:id} : delete the "id" valuesListItem.
     *
     * @param id the id of the valuesListItem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/values-list-items/{id}")
    public ResponseEntity<Void> deleteValuesListItem(@PathVariable Long id) {
        log.debug("REST request to delete ValuesListItem : {}", id);
        valuesListItemRepository.deleteById(id);
        valuesListItemSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/values-list-items?query=:query} : search for the valuesListItem corresponding
     * to the query.
     *
     * @param query the query of the valuesListItem search.
     * @return the result of the search.
     */
    @GetMapping("/_search/values-list-items")
    public List<ValuesListItem> searchValuesListItems(@RequestParam String query) {
        log.debug("REST request to search ValuesListItems for query {}", query);
        return StreamSupport
            .stream(valuesListItemSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
