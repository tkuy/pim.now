package fr.fastmarketeam.pimnow.repository;

import com.fasterxml.jackson.databind.JsonSerializable;
import fr.fastmarketeam.pimnow.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


    Optional<Product> findByIdFIgnoreCaseAndIsDeletedIsFalse(String idf);

    @Query(value = "select distinct product from Product product left join fetch product.categories where product.isDeleted=false",
        countQuery = "select count(distinct product) from Product product where product.isDeleted=false")
    Page<Product> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct product from Product product left join fetch product.categories where product.customer.id=:customerId and product.isDeleted=false",
        countQuery = "select count(distinct product) from Product product where product.customer.id=:customerId and product.isDeleted=false")
    Page<Product> findAllByCustomerIdWithEagerRelationships(Pageable pageable, @Param("customerId") Long customerId);

    Page<Product> findAllByCustomerIdAndIsDeletedIsFalse(Pageable pageable, Long customerId);

    @Query("select distinct product from Product product left join fetch product.categories where product.isDeleted=false")
    List<Product> findAllWithEagerRelationships();

    @Query("select product from Product product left join fetch product.categories where product.id =:id and product.customer.id=:customerId and product.isDeleted=false")
    Optional<Product> findOneWithEagerRelationships(@Param("id") Long id, @Param("customerId") Long customerId);

    Optional<Product> findByIdFIgnoreCaseAndCustomerIdAndIsDeletedIsFalse(String idf, Long customerId);

    List<Product> findAllByCustomerId(Long customerId);

    List<Product> findAllByCustomerIdAndIsDeleted(Long customerId, boolean isDeleted);

    int countProductsByCustomerIdAndIsDeletedIsFalse(Long customerId);

    int countProductsByCustomerIdAndIsDeletedIsTrue(Long customerId);

    int countAllByIsDeleted(boolean deleted);

    @Query("select product from Product product left join fetch product.categories where product.customer.id=:id and :idCategory in (select category.id from product.categories category) and product.isDeleted = false")
    List<Product> findAllByCustomerIdAndCategoryIdAndIsDeletedIsFalse(@Param("id")Long id, @Param("idCategory") Long idCategory);

    List<Product> findAllByCustomerIdAndFamilyIdAndIsDeletedIsFalse(Long id, Long idFamily);
}
