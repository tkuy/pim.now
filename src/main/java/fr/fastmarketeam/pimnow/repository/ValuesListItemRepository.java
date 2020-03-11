package fr.fastmarketeam.pimnow.repository;

import fr.fastmarketeam.pimnow.domain.ValuesListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ValuesListItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ValuesListItemRepository extends JpaRepository<ValuesListItem, Long> {

}
