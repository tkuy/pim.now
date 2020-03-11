package fr.fastmarketeam.pimnow.repository;

import fr.fastmarketeam.pimnow.domain.Category;
import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Category entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdFIgnoreCaseAndCustomerIdAndDeletedIsFalse(String idf, Long customerId);
    List<Category> findAllByCustomerAndDeletedIsFalse(Customer customer);

    @Query("select category from Category category left join fetch category.customer where category.id =:id and category.deleted = false")
    Optional<Category> findByIdAndCustomerWithEagerRelationship(@Param("id") Long id);

    Optional<Category> findByIdFAndDeletedAndCustomerId(String idF, boolean deleted, Long customerId);
}
