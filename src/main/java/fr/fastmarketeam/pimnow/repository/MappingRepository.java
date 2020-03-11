package fr.fastmarketeam.pimnow.repository;

import fr.fastmarketeam.pimnow.domain.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Mapping entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MappingRepository extends JpaRepository<Mapping, Long> {

    Optional<Mapping> findByIdAndCustomerId(Long id, Long customerId);

    Page<Mapping> findAllByCustomerId(Long customerId, Pageable pageable);
    List<Mapping> findAllByCustomerId(Long customerId);
    Optional<Mapping> findByIdFAndCustomerId(String idf, Long customerId);
}
