package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.Mapping;
import fr.fastmarketeam.pimnow.domain.*;
import fr.fastmarketeam.pimnow.repository.AssociationRepository;
import fr.fastmarketeam.pimnow.repository.AttributRepository;
import fr.fastmarketeam.pimnow.repository.MappingRepository;
import fr.fastmarketeam.pimnow.repository.UserExtraRepository;
import fr.fastmarketeam.pimnow.repository.search.MappingSearchRepository;
import fr.fastmarketeam.pimnow.security.AuthoritiesConstants;
import fr.fastmarketeam.pimnow.security.SecurityUtils;
import fr.fastmarketeam.pimnow.service.dto.AssociationDuplicateDTO;
import fr.fastmarketeam.pimnow.service.dto.MappingDuplicateDTO;
import fr.fastmarketeam.pimnow.service.util.UserCustomerUtil;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.Mapping}.
 */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\") or hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
public class MappingResource {

    private final Logger log = LoggerFactory.getLogger(MappingResource.class);

    private static final String ENTITY_NAME = "mapping";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MappingRepository mappingRepository;

    private final MappingSearchRepository mappingSearchRepository;

    private final UserExtraRepository userExtraRepository;

    private final AttributRepository attributRepository;

    private final AssociationRepository associationRepository;

    private final UserCustomerUtil userCustomerUtil;

    public MappingResource(MappingRepository mappingRepository, MappingSearchRepository mappingSearchRepository, UserExtraRepository userExtraRepository, AttributRepository attributRepository, AssociationRepository associationRepository, UserCustomerUtil userCustomerUtil) {
        this.mappingRepository = mappingRepository;
        this.mappingSearchRepository = mappingSearchRepository;
        this.userExtraRepository = userExtraRepository;
        this.attributRepository = attributRepository;
        this.associationRepository = associationRepository;
        this.userCustomerUtil = userCustomerUtil;
    }

    /**
     * {@code POST  /mappings} : Create a new mapping.
     *
     * @param mapping the mapping to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mapping, or with status {@code 400 (Bad Request)} if the mapping has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/mappings")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
    public ResponseEntity<Mapping> createMapping(@Valid @RequestBody Mapping mapping) throws URISyntaxException {
        log.debug("REST request to save Mapping : {}", mapping);
        if (mapping.getId() != null) {
            throw new BadRequestAlertException("A new mapping cannot already have an ID", ENTITY_NAME, "idexists");
        }
        List<Association> associationsToSave = new ArrayList<>();
        associationsToSave.addAll(mapping.getAssociations());
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        if(login.isPresent()){
            Optional<UserExtra> userExtra = userExtraRepository.findByUserLogin(login.get());
            userExtra.ifPresent(extra -> {
                mapping.setCustomer(extra.getCustomer());
                if(mappingRepository.findByIdFAndCustomerId(mapping.getIdF(), extra.getCustomer().getId()).isPresent()){
                    throw new BadRequestAlertException("Cannot create a mapping with an already existing idf", ENTITY_NAME, "alreadyexistingmapping");
                }
            });
        }
        mapping.setAssociations(null);
        Mapping result = mappingRepository.save(mapping);
        mappingSearchRepository.save(result);
        for(Association a : associationsToSave){
            Association association = new Association();
            Optional<Attribut> attribut = attributRepository.findByIdAndCustomerId(Long.valueOf(a.getIdFAttribut()), mapping.getCustomer().getId());
            if(attribut.isPresent()){
                association.setColumn(a.getColumn());
                association.setIdFAttribut(attribut.get().getIdF());
                association.setMapping(result);
                associationRepository.save(association);
            }
        }
        return ResponseEntity.created(new URI("/api/mappings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /mappings} : Updates an existing mapping.
     *
     * @param mapping the mapping to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mapping,
     * or with status {@code 400 (Bad Request)} if the mapping is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mapping couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Transactional
    @PutMapping("/mappings")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
    public ResponseEntity<Mapping> updateMapping(@Valid @RequestBody Mapping mapping) throws URISyntaxException {
        log.debug("REST request to edit Mapping : {}", mapping);

        List<Association> associationsToSave = new ArrayList<>();
        associationsToSave.addAll(mapping.getAssociations());
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        if(login.isPresent()){
            Optional<UserExtra> userExtra = userExtraRepository.findByUserLogin(login.get());
            userExtra.ifPresent(extra -> mapping.setCustomer(extra.getCustomer()));
        }

        associationRepository.deleteAllByMappingId(mapping.getId()) ;
        mapping.setAssociations(null);
        Mapping result = mappingRepository.save(mapping);
        mappingSearchRepository.save(result);
        for(Association a : associationsToSave){
            Association association = new Association();
            Optional<Attribut> attribut = attributRepository.findByIdAndCustomerId(Long.valueOf(a.getIdFAttribut()), mapping.getCustomer().getId());
            if(attribut.isPresent()){
                association.setColumn(a.getColumn());
                association.setIdFAttribut(attribut.get().getIdF());
                association.setMapping(result);
                associationRepository.save(association);
            }
        }
        return ResponseEntity.created(new URI("/api/mappings/" + result.getId()))
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getName()))
            .body(result);
    }

    /**
     * {@code GET  /mappings} : get all the mappings for a customer.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mappings in body.
     */
    @GetMapping("/mappings")
    public ResponseEntity<List<Mapping>> getAllMappings(Pageable pageable) {
        log.debug("REST request to get a page of Mappings");
        Customer currentCustomer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        Page<Mapping> page = mappingRepository.findAllByCustomerId(currentCustomer.getId(), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /mappings/:id} : get the "id" mapping.
     *
     * @param id the id of the mapping to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mapping, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/mappings/{id}")
    @Transactional
    public ResponseEntity<Mapping> getMapping(@PathVariable Long id) {
        log.debug("REST request to get Mapping : {}", id);
        Optional<Mapping> mapping = mappingRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(mapping);
    }

    @GetMapping("/mappings/duplicate/{id}")
    @Transactional
    public ResponseEntity<MappingDuplicateDTO> getMappingForDuplicate(@PathVariable Long id) {
        log.debug("REST request to get Mapping for duplicate : {}", id);
        MappingDuplicateDTO mappingDuplicateDTO = new MappingDuplicateDTO();
        Optional<Mapping> mapping = mappingRepository.findById(id);

        if(!mapping.isPresent()){
            throw new BadRequestAlertException("Mapping not found !", "mapping", "mapping_not_found");
        }
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        if(!login.isPresent()){
            throw new BadRequestAlertException("User not found !", "user", "user_not_found");
        }
        Optional<UserExtra> currentUserExtra = userExtraRepository.findByUserLogin(login.get());
        if(!currentUserExtra.isPresent()){
            throw new BadRequestAlertException("User not found !", "user", "user_not_found");
        }
        mappingDuplicateDTO.setMapping(mapping.get());
        for(Association a : mapping.get().getAssociations()){
            AssociationDuplicateDTO associationDuplicateDTO = new AssociationDuplicateDTO();
            Optional<Attribut> attribut = attributRepository.findByIdFIgnoreCaseAndCustomerId(a.getIdFAttribut(), currentUserExtra.get().getCustomer().getId());
            if(!attribut.isPresent()){
                throw new BadRequestAlertException("Attribut not found !", "attribut", "attribut_not_found");
            }
            associationDuplicateDTO.setIdAttribut(attribut.get().getId());
            associationDuplicateDTO.setNameAttribut(attribut.get().getNom());
            associationDuplicateDTO.setColumn(a.getColumn());
            mappingDuplicateDTO.getAssociations().add(associationDuplicateDTO);
        }
        return ResponseUtil.wrapOrNotFound(Optional.of(mappingDuplicateDTO));
    }

    /**
     * {@code GET  /mappings/customer} : get the mappings related to the customer of the current user
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mappings, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/mappings/customer")
    public ResponseEntity<List<Mapping>> getMappingsByCustomer() {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        List<Mapping> mappings = new ArrayList<>();
        if(login.isPresent()){
            Optional<UserExtra> userExtra = userExtraRepository.findByUserLogin(login.get());
            if(userExtra.isPresent()){
                mappings = mappingRepository.findAllByCustomerId(userExtra.get().getCustomer().getId());
            }
        }
        return ResponseEntity.ok().body(mappings);
    }

    /**
     * {@code DELETE  /mappings/:id} : delete the "id" mapping.
     *
     * @param id the id of the mapping to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/mappings/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
    public ResponseEntity<Void> deleteMapping(@PathVariable Long id) {
        log.debug("REST request to delete Mapping : {}", id);
        mappingRepository.deleteById(id);
        mappingSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/mappings?query=:query} : search for the mapping corresponding
     * to the query.
     *
     * @param query the query of the mapping search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/mappings")
    public ResponseEntity<List<Mapping>> searchMappings(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Mappings for query {}", query);
        Page<Mapping> page = mappingSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
