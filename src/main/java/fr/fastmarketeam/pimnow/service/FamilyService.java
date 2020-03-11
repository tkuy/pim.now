package fr.fastmarketeam.pimnow.service;

import fr.fastmarketeam.pimnow.domain.Attribut;
import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Family;

import java.util.Optional;
import java.util.Set;

public interface FamilyService {
    Family createFamily(Family family, Optional<Family> predecessor, Customer customer, Set<Attribut> newAttributes, Set<Attribut> newExistingAttributes);
    Family updateFamily(Family family, Set<Attribut> newAttributes, Set<Attribut> newExistingAttributes);
    Set<Attribut> getFamilyAttributs(Family family);
    Optional<Family> findByIdAndCustomerID(Long id, long customerId);
    boolean deleteFamilyById(Long id);
}
