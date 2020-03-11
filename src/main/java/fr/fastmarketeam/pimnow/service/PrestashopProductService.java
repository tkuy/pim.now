package fr.fastmarketeam.pimnow.service;

import fr.fastmarketeam.pimnow.domain.PrestashopProduct;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link PrestashopProduct}.
 */
public interface PrestashopProductService {

    /**
     * Save a prestashopProduct.
     *
     * @param prestashopProduct the entity to save.
     * @return the persisted entity.
     */
    PrestashopProduct save(PrestashopProduct prestashopProduct);

    /**
     * Get all the prestashopProducts.
     *
     * @return the list of entities.
     */
    List<PrestashopProduct> findAll();


    /**
     * Get the "id" prestashopProduct.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PrestashopProduct> findOne(Long id);

    /**
     * Delete the "id" prestashopProduct.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the prestashopProduct corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<PrestashopProduct> search(String query);
}
