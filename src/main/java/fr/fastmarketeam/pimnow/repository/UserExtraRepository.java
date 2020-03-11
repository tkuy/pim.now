package fr.fastmarketeam.pimnow.repository;

import fr.fastmarketeam.pimnow.domain.UserExtra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the UserExtra entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserExtraRepository extends JpaRepository<UserExtra, Long> {

    Page<UserExtra> findAllByCustomerId(Pageable pageable, Long id);
    Optional<UserExtra> findByUserLogin(String login);
    List<UserExtra> findAllByCustomerIdAndUserActivated(Long id, boolean activated);
    int countByCustomerIdAndUserActivated(Long customerId, boolean activated);
}
