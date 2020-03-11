package fr.fastmarketeam.pimnow.service.impl;

import fr.fastmarketeam.pimnow.domain.Attribut;
import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Family;
import fr.fastmarketeam.pimnow.repository.AttributRepository;
import fr.fastmarketeam.pimnow.repository.FamilyRepository;

import fr.fastmarketeam.pimnow.repository.ProductRepository;
import fr.fastmarketeam.pimnow.repository.search.FamilySearchRepository;
import fr.fastmarketeam.pimnow.security.SecurityUtils;
import fr.fastmarketeam.pimnow.service.FamilyService;
import fr.fastmarketeam.pimnow.service.errors.*;
import fr.fastmarketeam.pimnow.service.util.UserCustomerUtil;
import fr.fastmarketeam.pimnow.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class FamilyServiceImpl  implements FamilyService {

    private final Logger log = LoggerFactory.getLogger(FamilyServiceImpl.class);
    @Autowired
    private FamilyRepository familyRepository;
    @Autowired
    private AttributRepository attributRepository;
    @Autowired
    private UserCustomerUtil userCustomerUtil;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private FamilySearchRepository familySearchRepository;

    @Override
    @Transactional
    public Family createFamily(Family family, Optional<Family> predecessor, Customer customer, Set<Attribut> newAttributes, Set<Attribut> newExistingAttributes) {
        log.info("Create family a new family " + family + " with " + newAttributes.size() + " new attributes");
        if(customer == null) {
            throw new CustomerRequiredActionException();
        }
        Family predecessorTmp;
        if(predecessor.isPresent()) {
            predecessorTmp = predecessor.get();
        } else {
            Long id = Long.valueOf(customer.getFamilyRoot());
            Optional<Family> optPredecessor=familyRepository.findOneWithEagerRelationships(id);
            if (!optPredecessor.isPresent()) {
                throw new IllegalStateException("Predecessor not found");
            }
            predecessorTmp =optPredecessor.get();
        }
        Family familyExample = new Family();
        familyExample.setIdF(family.getIdF());
        familyExample.setCustomer(customer);
        if(familyRepository.findOne(Example.of(familyExample)).isPresent()) {
            throw new FunctionalIDAlreadyUsedException();
        }
        Set<Attribut> attributes = newAttributes.stream().
            peek(attribut -> {
                Optional<Attribut> attributOpt = attributRepository.findByIdFIgnoreCaseAndCustomerId(attribut.getIdF(), customer.getId());
                if(attributOpt.isPresent()) {
                    throw new FunctionalIDAlreadyUsedException();
                }
                attribut.setCustomer(customer);
            }).filter(attribut -> attribut.getNom() != null && attribut.getType() != null)
            .collect(Collectors.toSet());
        List<Attribut> results = attributRepository.saveAll(attributes);
        results.forEach(family::addAttribute);
        if(newExistingAttributes != null) {
            List<Long> ids = newExistingAttributes.stream().filter(attribut -> attribut != null && attribut.getId() != null).mapToLong(Attribut::getId).boxed().collect(Collectors.toList());
            List<Attribut> retrievedAttributs = attributRepository.findAllById(ids);
            retrievedAttributs.forEach(family::addAttribute);
        }
        family.setCustomer(customer);
        //Add the successor to the parent
        Family familySaved = familyRepository.save(family);
        predecessorTmp.addSuccessors(familySaved);
        familyRepository.save(predecessorTmp.addSuccessors(familySaved));
        return familySaved;
    }

    @Override
    @Transactional
    public Family updateFamily(Family family, Set<Attribut> newAttributes, Set<Attribut> newExistingAttributes) {
        log.info("Create family a new family " + family + " with " + newAttributes.size() + " new attributes");
        Optional<Family> familyOpt = familyRepository.findOneWithEagerRelationships(family.getId());
        if(!familyOpt.isPresent()) {
            throw new IllegalStateException("The family can't be updated because it doesn't exist");
        }
        Family family1 = familyOpt.get();
        family.setFamily(family1.getFamily());
        // New attributs
        if(newExistingAttributes != null) {
            List<Long> ids = newExistingAttributes.stream().filter(attribut -> attribut != null && attribut.getId() != null).mapToLong(Attribut::getId).boxed().collect(Collectors.toList());
            List<Attribut> retrievedAttributs = attributRepository.findAllById(ids);
            retrievedAttributs.forEach(family::addAttribute);
        }
        Set<Attribut> attributes = newAttributes.stream().
            peek(attribut -> {
                Optional<Attribut> attributOpt = attributRepository.findByIdFIgnoreCaseAndCustomerId(attribut.getIdF(), family.getCustomer().getId());
                if(attributOpt.isPresent()) {
                    throw new FunctionalIDAlreadyUsedException();
                }
                attribut.setCustomer(family.getCustomer());
            }).filter(attribut -> attribut.getNom() != null && attribut.getType() != null)
            .collect(Collectors.toSet());
        List<Attribut> results = attributRepository.saveAll(attributes);
        // Add the existing new attributs
        if(newExistingAttributes != null) {
            results.forEach(family::addAttribute);
            List<Long> ids = newExistingAttributes.stream().filter(attribut -> attribut != null && attribut.getId() != null).mapToLong(Attribut::getId).boxed().collect(Collectors.toList());
            List<Attribut> retrievedAttributs = attributRepository.findAllById(ids);
            retrievedAttributs.forEach(family::addAttribute);
        }
        return familyRepository.save(family);
    }


    @Override
    public Set<Attribut> getFamilyAttributs(Family family) {
        Set<Attribut> set = new HashSet<>(family.getAttributes());
        if(family.getFamily() == null) {
            return set;
        }
        Family predecessor = family.getFamily();
        return addAttributs(set,predecessor);
    }

    @Override
    public Optional<Family> findByIdAndCustomerID(Long id, long customerId) {
        return familyRepository.findOneWithEagerRelationshipsByCustomerId(id,customerId);
    }

    private Set<Attribut> addAttributs(Set<Attribut> attributs, Family predecessor) {
        attributs.addAll(predecessor.getAttributes());
        return predecessor.getFamily() != null ? addAttributs(attributs, predecessor.getFamily()) : attributs ;
    }
    @Override
    public boolean deleteFamilyById(Long id) {
        Optional<Family> optF = familyRepository.findOneWithEagerRelationships(id);

        if (!optF.isPresent()) {
            return true;
        }

        Family f = optF.get();
        Customer userCustomer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        if (!userCustomer.getId().equals(f.getCustomer().getId())) {
            return true;
        }

        if (id.longValue() == userCustomer.getFamilyRoot().longValue()) {
            throw new RootFamilyException();
        }

        if (f.getSuccessors().stream().filter(fam -> !fam.isDeleted()).count() != 0) {
            throw new SuccessorFamilyStillAliveException();
        }

        if (!productRepository.findAllByCustomerIdAndFamilyIdAndIsDeletedIsFalse(userCustomer.getId(), id).isEmpty()) {
            throw new ProductStillAttachedToFamilyException();
        }

        f.setDeleted(true);
        familyRepository.save(f);
        familySearchRepository.deleteById(id);
        return false;
    }
}
