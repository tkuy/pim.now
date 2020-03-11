package fr.fastmarketeam.pimnow.repository;

import fr.fastmarketeam.pimnow.domain.AttributValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the AttributValue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttributValueRepository extends JpaRepository<AttributValue, Long> {
    Optional<AttributValue> findByAttributIdAndProductId(Long attributId, Long productId);
    List<Optional<AttributValue>> findByProductId(Long productId);
    List<AttributValue> findAllByProduct_IdOrderByAttributIdF(Long productId);
    void deleteByIdIn(List<Long> ids);
}
