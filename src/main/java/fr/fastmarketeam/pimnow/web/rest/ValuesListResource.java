package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.ValuesList;
import fr.fastmarketeam.pimnow.repository.ValuesListRepository;
import fr.fastmarketeam.pimnow.repository.search.ValuesListSearchRepository;
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
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.ValuesList}.
 */
@RestController
@RequestMapping("/api")
public class ValuesListResource {

    private final Logger log = LoggerFactory.getLogger(ValuesListResource.class);

    private static final String ENTITY_NAME = "valuesList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ValuesListRepository valuesListRepository;

    private final ValuesListSearchRepository valuesListSearchRepository;

    public ValuesListResource(ValuesListRepository valuesListRepository, ValuesListSearchRepository valuesListSearchRepository) {
        this.valuesListRepository = valuesListRepository;
        this.valuesListSearchRepository = valuesListSearchRepository;
    }

    /**
     * {@code POST  /values-lists} : Create a new valuesList.
     *
     * @param valuesList the valuesList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new valuesList, or with status {@code 400 (Bad Request)} if the valuesList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/values-lists")
    public ResponseEntity<ValuesList> createValuesList(@Valid @RequestBody ValuesList valuesList) throws URISyntaxException {
        log.debug("REST request to save ValuesList : {}", valuesList);
        if (valuesList.getId() != null) {
            throw new BadRequestAlertException("A new valuesList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ValuesList result = valuesListRepository.save(valuesList);
        valuesListSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/values-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /values-lists} : Updates an existing valuesList.
     *
     * @param valuesList the valuesList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated valuesList,
     * or with status {@code 400 (Bad Request)} if the valuesList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the valuesList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/values-lists")
    public ResponseEntity<ValuesList> updateValuesList(@Valid @RequestBody ValuesList valuesList) throws URISyntaxException {
        log.debug("REST request to update ValuesList : {}", valuesList);
        if (valuesList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ValuesList result = valuesListRepository.save(valuesList);
        valuesListSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, valuesList.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /values-lists} : get all the valuesLists.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of valuesLists in body.
     */
    @GetMapping("/values-lists")
    public List<ValuesList> getAllValuesLists() {
        log.debug("REST request to get all ValuesLists");
        return valuesListRepository.findAll();
    }

    /**
     * {@code GET  /values-lists/:id} : get the "id" valuesList.
     *
     * @param id the id of the valuesList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the valuesList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/values-lists/{id}")
    public ResponseEntity<ValuesList> getValuesList(@PathVariable Long id) {
        log.debug("REST request to get ValuesList : {}", id);
        Optional<ValuesList> valuesList = valuesListRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(valuesList);
    }

    /**
     * {@code DELETE  /values-lists/:id} : delete the "id" valuesList.
     *
     * @param id the id of the valuesList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/values-lists/{id}")
    public ResponseEntity<Void> deleteValuesList(@PathVariable Long id) {
        log.debug("REST request to delete ValuesList : {}", id);
        valuesListRepository.deleteById(id);
        valuesListSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/values-lists?query=:query} : search for the valuesList corresponding
     * to the query.
     *
     * @param query the query of the valuesList search.
     * @return the result of the search.
     */
    @GetMapping("/_search/values-lists")
    public List<ValuesList> searchValuesLists(@RequestParam String query) {
        log.debug("REST request to search ValuesLists for query {}", query);
        return StreamSupport
            .stream(valuesListSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
