package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.Attribut;
import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Family;
import fr.fastmarketeam.pimnow.repository.FamilyRepository;
import fr.fastmarketeam.pimnow.repository.ProductRepository;
import fr.fastmarketeam.pimnow.repository.search.FamilySearchRepository;
import fr.fastmarketeam.pimnow.security.AuthoritiesConstants;
import fr.fastmarketeam.pimnow.security.SecurityUtils;
import fr.fastmarketeam.pimnow.service.FamilyService;
import fr.fastmarketeam.pimnow.service.util.UserCustomerUtil;
import fr.fastmarketeam.pimnow.web.rest.errors.BadRequestAlertException;
import fr.fastmarketeam.pimnow.web.rest.errors.CustomerRequiredActionException;
import fr.fastmarketeam.pimnow.web.rest.vm.FamilyVM;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.Family}.
 */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\") or hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
public class FamilyResource {

    private final Logger log = LoggerFactory.getLogger(FamilyResource.class);

    private static final String ENTITY_NAME = "family";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FamilyRepository familyRepository;

    private final FamilySearchRepository familySearchRepository;

    private final FamilyService familyService;

    private final UserCustomerUtil userCustomerUtil;

    private final ProductRepository productRepository;

    public FamilyResource(FamilyRepository familyRepository, FamilySearchRepository familySearchRepository, FamilyService familyService, UserCustomerUtil userCustomerUtil, ProductRepository productRepository) {
        this.familyRepository = familyRepository;
        this.familySearchRepository = familySearchRepository;
        this.familyService = familyService;
        this.userCustomerUtil = userCustomerUtil;
        this.productRepository = productRepository;
    }

    /**
     * {@code POST  /families} : Create a new family.
     *
     * @param family the family to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new family, or with status {@code 400 (Bad Request)} if the family has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/families")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
    public ResponseEntity<Family> createFamily(@Valid @RequestBody FamilyVM family) throws URISyntaxException {
        log.debug("REST request to save Family : {}", family);

        if (family.getId() != null) {
            throw new BadRequestAlertException("A new family cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Optional<Family> parentOpt = familyRepository.findOneWithEagerRelationships(family.getIdPredecessor());
        Family result = familyService.createFamily(
            family.toFamily(),
            parentOpt,
            userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin()),
            family.getNewAttributes(),
            family.getNewExistingAttributes());
        familySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/families/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getNom()))
            .body(result);
    }

    /**
     * {@code PUT  /families} : Updates an existing family.
     *
     * @param family the family to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated family,
     * or with status {@code 400 (Bad Request)} if the family is not valid,
     * or with status {@code 500 (Internal Server Error)} if the family couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/families")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
    public ResponseEntity<Family> updateFamily(@Valid @RequestBody FamilyVM family) throws URISyntaxException {
        log.debug("REST request to update Family : {}", family);
        if (family.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<Family> f = familyRepository.findOneWithEagerRelationships(family.getId());
        Customer customer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        if(!f.isPresent()) {
            throw new NoSuchElementException("The family is not found");
        }
        if(!customer.getId().equals(f.get().getCustomer().getId())) {
            throw new CustomerRequiredActionException();
        }
        Family result = familyService.updateFamily(family.toFamily(), family.getNewAttributes(), family.getNewExistingAttributes());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, family.getNom()))
            .body(result);
    }


    @GetMapping("/families/root")
    public ResponseEntity<Family> getFamilyRoot() {
        log.debug("REST request to get the family root");
        Long id = Long.valueOf(userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin()).getFamilyRoot());
        Optional<Family> optionalFamily = familyRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(optionalFamily);
    }

    /**
     * {@code GET  /families} : get all the families.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of families in body.
     */
    @GetMapping("/families")
    public List<Family> getAllFamilies(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Families");
        return familyRepository.findAllByCustomerIdAndDeletedIsFalse(userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin()).getId());
    }

    /**
     * {@code GET  /families/:id} : get the "id" family.
     *
     * @param id the id of the family to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the family, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/families/{id}")
    public ResponseEntity<Family> getFamily(@PathVariable Long id) {
        log.debug("REST request to get Family : {}", id);

        Long customerId = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin()).getId();
        Optional<Family> family = familyService.findByIdAndCustomerID(id, customerId);

        return ResponseUtil.wrapOrNotFound(family);
    }


    @GetMapping("/families/familyAttributs/{id}")
    public Set<Attribut> getAttributsByFamily(@PathVariable Long id) {
        log.debug("REST request to get Family : {}", id);
        Customer customer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        Optional<Family> family = familyRepository.findOneWithEagerRelationships(id);

        if (family.isPresent() && family.get().getCustomer().getId().equals(customer.getId())) {
            return familyService.getFamilyAttributs(family.get());
        }
        return new HashSet<>();
    }

    /**
     * {@code DELETE  /families/:id} : delete the "id" family.
     *
     * @param id the id of the family to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/families/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
    public ResponseEntity<Void> deleteFamily(@PathVariable Long id) {
        log.debug("REST request to delete Family : {}", id);
        if (familyService.deleteFamilyById(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
