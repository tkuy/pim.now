package fr.fastmarketeam.pimnow.repository;

import fr.fastmarketeam.pimnow.domain.ValuesList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ValuesList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ValuesListRepository extends JpaRepository<ValuesList, Long> {

}
