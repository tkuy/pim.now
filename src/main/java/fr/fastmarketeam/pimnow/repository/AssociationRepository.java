package fr.fastmarketeam.pimnow.repository;

import fr.fastmarketeam.pimnow.domain.Association;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Association entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssociationRepository extends JpaRepository<Association, Long> {
    void deleteAllByMappingId(Long idMapping);
}
