package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.PrestashopProduct;
import fr.fastmarketeam.pimnow.security.AuthoritiesConstants;
import fr.fastmarketeam.pimnow.service.PrestashopProductService;
import fr.fastmarketeam.pimnow.web.rest.errors.BadRequestAlertException;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.PrestashopProduct}.
 */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\") or hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
public class PrestashopProductResource {

    private final Logger log = LoggerFactory.getLogger(PrestashopProductResource.class);

    private static final String ENTITY_NAME = "prestashopProduct";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PrestashopProductService prestashopProductService;

    public PrestashopProductResource(PrestashopProductService prestashopProductService) {
        this.prestashopProductService = prestashopProductService;
    }

    /**
     * {@code POST  /prestashop-products} : Create a new prestashopProduct.
     *
     * @param prestashopProduct the prestashopProduct to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new prestashopProduct, or with status {@code 400 (Bad Request)} if the prestashopProduct has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/prestashop-products")
    public ResponseEntity<PrestashopProduct> createPrestashopProduct(@Valid @RequestBody PrestashopProduct prestashopProduct) throws URISyntaxException {
        log.debug("REST request to save PrestashopProduct : {}", prestashopProduct);
        if (prestashopProduct.getId() != null) {
            throw new BadRequestAlertException("A new prestashopProduct cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(prestashopProduct.getProductPim())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        PrestashopProduct result = prestashopProductService.save(prestashopProduct);
        return ResponseEntity.created(new URI("/api/prestashop-products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /prestashop-products} : Updates an existing prestashopProduct.
     *
     * @param prestashopProduct the prestashopProduct to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated prestashopProduct,
     * or with status {@code 400 (Bad Request)} if the prestashopProduct is not valid,
     * or with status {@code 500 (Internal Server Error)} if the prestashopProduct couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/prestashop-products")
    public ResponseEntity<PrestashopProduct> updatePrestashopProduct(@Valid @RequestBody PrestashopProduct prestashopProduct) throws URISyntaxException {
        log.debug("REST request to update PrestashopProduct : {}", prestashopProduct);
        if (prestashopProduct.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PrestashopProduct result = prestashopProductService.save(prestashopProduct);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, prestashopProduct.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /prestashop-products} : get all the prestashopProducts.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of prestashopProducts in body.
     */
    @GetMapping("/prestashop-products")
    public List<PrestashopProduct> getAllPrestashopProducts() {
        log.debug("REST request to get all PrestashopProducts");
        return prestashopProductService.findAll();
    }

    /**
     * {@code GET  /prestashop-products/:id} : get the "id" prestashopProduct.
     *
     * @param id the id of the prestashopProduct to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the prestashopProduct, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/prestashop-products/{id}")
    public ResponseEntity<PrestashopProduct> getPrestashopProduct(@PathVariable Long id) {
        log.debug("REST request to get PrestashopProduct : {}", id);
        Optional<PrestashopProduct> prestashopProduct = prestashopProductService.findOne(id);
        return ResponseUtil.wrapOrNotFound(prestashopProduct);
    }

    /**
     * {@code DELETE  /prestashop-products/:id} : delete the "id" prestashopProduct.
     *
     * @param id the id of the prestashopProduct to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/prestashop-products/{id}")
    public ResponseEntity<Void> deletePrestashopProduct(@PathVariable Long id) {
        log.debug("REST request to delete PrestashopProduct : {}", id);
        prestashopProductService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/prestashop-products?query=:query} : search for the prestashopProduct corresponding
     * to the query.
     *
     * @param query the query of the prestashopProduct search.
     * @return the result of the search.
     */
    @GetMapping("/_search/prestashop-products")
    public List<PrestashopProduct> searchPrestashopProducts(@RequestParam String query) {
        log.debug("REST request to search PrestashopProducts for query {}", query);
        return prestashopProductService.search(query);
    }

}
