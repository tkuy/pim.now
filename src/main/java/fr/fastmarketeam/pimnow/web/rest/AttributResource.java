package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.Attribut;
import fr.fastmarketeam.pimnow.repository.AttributRepository;
import fr.fastmarketeam.pimnow.repository.search.AttributSearchRepository;
import fr.fastmarketeam.pimnow.security.SecurityUtils;
import fr.fastmarketeam.pimnow.service.util.UserCustomerUtil;
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
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.Attribut}.
 */
@RestController
@RequestMapping("/api")
public class AttributResource {

    private final Logger log = LoggerFactory.getLogger(AttributResource.class);

    private static final String ENTITY_NAME = "attribut";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AttributRepository attributRepository;

    private final AttributSearchRepository attributSearchRepository;

    private final UserCustomerUtil userCustomerUtil;

    public AttributResource(AttributRepository attributRepository, AttributSearchRepository attributSearchRepository, UserCustomerUtil userCustomerUtil) {
        this.attributRepository = attributRepository;
        this.attributSearchRepository = attributSearchRepository;
        this.userCustomerUtil = userCustomerUtil;
    }

    /**
     * {@code POST  /attributs} : Create a new attribut.
     *
     * @param attribut the attribut to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new attribut, or with status {@code 400 (Bad Request)} if the attribut has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/attributs")
    public ResponseEntity<Attribut> createAttribut(@Valid @RequestBody Attribut attribut) throws URISyntaxException {
        log.debug("REST request to save Attribut : {}", attribut);
        if (attribut.getId() != null) {
            throw new BadRequestAlertException("A new attribut cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Attribut result = attributRepository.save(attribut);
        attributSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/attributs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /attributs} : Updates an existing attribut.
     *
     * @param attribut the attribut to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attribut,
     * or with status {@code 400 (Bad Request)} if the attribut is not valid,
     * or with status {@code 500 (Internal Server Error)} if the attribut couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/attributs")
    public ResponseEntity<Attribut> updateAttribut(@Valid @RequestBody Attribut attribut) throws URISyntaxException {
        log.debug("REST request to update Attribut : {}", attribut);
        if (attribut.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Attribut result = attributRepository.save(attribut);
        attributSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, attribut.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /attributs} : get all the attributs.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of attributs in body.
     */
    @GetMapping("/attributs")
    public List<Attribut> getAllAttributs() {
        log.debug("REST request to get all Attributs");
        return attributRepository.findAllByCustomerId(userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin()).getId());
    }

    /**
     * {@code GET  /attributs/:id} : get the "id" attribut.
     *
     * @param id the id of the attribut to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attribut, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/attributs/{id}")
    public ResponseEntity<Attribut> getAttribut(@PathVariable Long id) {
        log.debug("REST request to get Attribut : {}", id);
        Long customerId = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin()).getId();
        Optional<Attribut> attribut = attributRepository.findByIdAndCustomerId(id, customerId);
        return ResponseUtil.wrapOrNotFound(attribut);
    }

    /**
     * {@code DELETE  /attributs/:id} : delete the "id" attribut.
     *
     * @param id the id of the attribut to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/attributs/{id}")
    public ResponseEntity<Void> deleteAttribut(@PathVariable Long id) {
        log.debug("REST request to delete Attribut : {}", id);
        attributRepository.deleteById(id);
        attributSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/attributs?query=:query} : search for the attribut corresponding
     * to the query.
     *
     * @param query the query of the attribut search.
     * @return the result of the search.
     */
    @GetMapping("/_search/attributs")
    public List<Attribut> searchAttributs(@RequestParam String query) {
        log.debug("REST request to search Attributs for query {}", query);
        return StreamSupport
            .stream(attributSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
