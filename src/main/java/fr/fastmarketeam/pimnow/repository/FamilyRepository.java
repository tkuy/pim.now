package fr.fastmarketeam.pimnow.repository;

import fr.fastmarketeam.pimnow.domain.Family;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Family entity.
 */
@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {

    @Query(value = "select distinct family from Family family left join fetch family.attributes where family.deleted=false",
        countQuery = "select count(distinct family) from Family family where family.deleted=false")
    Page<Family> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct family from Family family left join fetch family.attributes where family.deleted=false")
    List<Family> findAllWithEagerRelationships();

    @Query("select family from Family family left join fetch family.attributes where family.id =:id and family.deleted=false")
    Optional<Family> findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select family from Family family left join fetch family.attributes where family.id =:id and family.customer.id=:customerId and family.deleted=false")
    Optional<Family> findOneWithEagerRelationshipsByCustomerId(@Param("id") Long id, @Param("customerId") Long customerId);

    List<Family> findAllByCustomerIdAndDeletedIsFalse(Long customerId);
    Optional<Family> findByIdFIgnoreCaseAndCustomerIdAndDeletedIsFalse(String idf, Long customerId);

}
