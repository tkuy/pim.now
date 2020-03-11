package fr.fastmarketeam.pimnow.repository;

import fr.fastmarketeam.pimnow.domain.PrestashopProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the PrestashopProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PrestashopProductRepository extends JpaRepository<PrestashopProduct, Long> {
    Optional<PrestashopProduct> findByProductPimId(Long productPimId);
    int countPrestashopProductByProductPimCustomerId(Long customerId);

    long count();
}
