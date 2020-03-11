package fr.fastmarketeam.pimnow.service.impl;

import fr.fastmarketeam.pimnow.domain.PrestashopProduct;
import fr.fastmarketeam.pimnow.repository.PrestashopProductRepository;
import fr.fastmarketeam.pimnow.repository.ProductRepository;
import fr.fastmarketeam.pimnow.repository.search.PrestashopProductSearchRepository;
import fr.fastmarketeam.pimnow.service.PrestashopProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing {@link PrestashopProduct}.
 */
@Service
@Transactional
public class PrestashopProductServiceImpl implements PrestashopProductService {

    private final Logger log = LoggerFactory.getLogger(PrestashopProductServiceImpl.class);

    private final PrestashopProductRepository prestashopProductRepository;

    private final PrestashopProductSearchRepository prestashopProductSearchRepository;

    private final ProductRepository productRepository;

    public PrestashopProductServiceImpl(PrestashopProductRepository prestashopProductRepository, PrestashopProductSearchRepository prestashopProductSearchRepository, ProductRepository productRepository) {
        this.prestashopProductRepository = prestashopProductRepository;
        this.prestashopProductSearchRepository = prestashopProductSearchRepository;
        this.productRepository = productRepository;
    }

    /**
     * Save a prestashopProduct.
     *
     * @param prestashopProduct the entity to save.
     * @return the persisted entity.
     */
    @Override
    public PrestashopProduct save(PrestashopProduct prestashopProduct) {
        log.debug("Request to save PrestashopProduct : {}", prestashopProduct);
        long productId = prestashopProduct.getProductPim().getId();
        productRepository.findById(productId).ifPresent(prestashopProduct::productPim);
        PrestashopProduct result = prestashopProductRepository.save(prestashopProduct);
        prestashopProductSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the prestashopProducts.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PrestashopProduct> findAll() {
        log.debug("Request to get all PrestashopProducts");
        return prestashopProductRepository.findAll();
    }


    /**
     * Get one prestashopProduct by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PrestashopProduct> findOne(Long id) {
        log.debug("Request to get PrestashopProduct : {}", id);
        return prestashopProductRepository.findById(id);
    }

    /**
     * Delete the prestashopProduct by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete PrestashopProduct : {}", id);
        prestashopProductRepository.deleteById(id);
        prestashopProductSearchRepository.deleteById(id);
    }

    /**
     * Search for the prestashopProduct corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PrestashopProduct> search(String query) {
        log.debug("Request to search PrestashopProducts for query {}", query);
        return StreamSupport
            .stream(prestashopProductSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
