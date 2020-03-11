package fr.fastmarketeam.pimnow.repository;
import fr.fastmarketeam.pimnow.domain.ConfigurationCustomer;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ConfigurationCustomer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConfigurationCustomerRepository extends JpaRepository<ConfigurationCustomer, Long> {

}
