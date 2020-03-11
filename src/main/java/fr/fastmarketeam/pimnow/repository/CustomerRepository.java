package fr.fastmarketeam.pimnow.repository;

import fr.fastmarketeam.pimnow.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Customer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Page<Customer> findAllByIsDeleted(Pageable pageable, Boolean isDeleted);
    int countAllByIsDeleted(boolean isDeleted);
}
