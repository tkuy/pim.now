package fr.fastmarketeam.pimnow.repository;

import fr.fastmarketeam.pimnow.domain.AttributValuesList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the AttributValuesList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttributValuesListRepository extends JpaRepository<AttributValuesList, Long> {

}
