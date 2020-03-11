package fr.fastmarketeam.pimnow.repository;

import fr.fastmarketeam.pimnow.domain.Attribut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Attribut entity.
 */
@Repository
public interface AttributRepository extends JpaRepository<Attribut, Long> {

    Optional<Attribut> findByIdFIgnoreCaseAndCustomerId(String idF, Long customerId);
    Optional<Attribut> findByIdAndCustomerId(Long id, Long customerId);
    List<Attribut> findAllByCustomerId(Long customerId);
    List<Attribut> findByNomIgnoreCaseAndCustomerId(String nom, Long customerId);
}
