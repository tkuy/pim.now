package fr.fastmarketeam.pimnow.service.impl;

import fr.fastmarketeam.pimnow.domain.Attribut;
import fr.fastmarketeam.pimnow.domain.Category;
import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Family;
import fr.fastmarketeam.pimnow.domain.enumeration.AttributType;
import fr.fastmarketeam.pimnow.repository.AttributRepository;
import fr.fastmarketeam.pimnow.repository.CategoryRepository;
import fr.fastmarketeam.pimnow.repository.CustomerRepository;
import fr.fastmarketeam.pimnow.repository.FamilyRepository;
import fr.fastmarketeam.pimnow.repository.search.CategorySearchRepository;
import fr.fastmarketeam.pimnow.repository.search.FamilySearchRepository;
import fr.fastmarketeam.pimnow.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final FamilyRepository familyRepository;

    private final CustomerRepository customerRepository;

    private final AttributRepository attributRepository;

    private final FamilySearchRepository familySearchRepository;

    private final CategoryRepository categoryRepository;

    private final CategorySearchRepository categorySearchRepository;

    public CustomerServiceImpl(FamilyRepository familyRepository, CustomerRepository customerRepository, FamilySearchRepository familySearchRepository, CategoryRepository categoryRepository, CategorySearchRepository categorySearchRepository, AttributRepository attributRepository) {
        this.familyRepository = familyRepository;
        this.customerRepository = customerRepository;
        this.familySearchRepository = familySearchRepository;
        this.categoryRepository = categoryRepository;
        this.categorySearchRepository = categorySearchRepository;
        this.attributRepository = attributRepository;
    }

    private Category newRootCategory(Customer customer) {
        Category c = new Category();
        c.setCustomer(customer);
        c.setNom("Category root");
        c.setIdF("CATEGORY_ROOT");

        return c;
    }

    private Family newRootFamily(Customer customer) {
        Family f = new Family();
        f.setCustomer(customer);
        f.setNom("Family root");
        f.setIdF("FAMILY_ROOT");

        return f;
    }
    private Attribut createNewAttribute(String idf, String name, AttributType type, Family family, Customer c) {
        Attribut attr = new Attribut();
        // Modifier avec les constantes ajouté par la gestions de l'import
        attr.setIdF(idf);
        attr.setNom(name);
        attr.setType(type);
        attr.addFamily(family);
        attr.setCustomer(c);
        return attr;
    }

    private void createAndSaveAttribute(String idf, String name, AttributType type, Family family, Customer c) {
        attributRepository.save(createNewAttribute(idf, name, type, family, c));
    }

    private void addDefaultAttributesInRootFamily(Family root, Customer customer) {
        createAndSaveAttribute(Attribut.idFAttributIdF, "Id Fonctionnel", AttributType.TEXT, root, customer);
        createAndSaveAttribute(Attribut.idFAttributNom, "Nom", AttributType.TEXT, root, customer);
        createAndSaveAttribute(Attribut.idFAttributDescription, "Description", AttributType.TEXT, root, customer);
        createAndSaveAttribute(Attribut.idFAttributFamille, "Famille", AttributType.TEXT, root, customer);
        createAndSaveAttribute(Attribut.idFAttributCategorie, "Categorie", AttributType.TEXT, root, customer);
        createAndSaveAttribute(Attribut.idFAttributPrix, "Prix", AttributType.NUMBER, root, customer);
        createAndSaveAttribute(Attribut.idFAttributStock, "Stock", AttributType.NUMBER, root, customer);
    }

    /**
     * This function create the roots of the customer (family and category). It is used when creating a new customer.
     * @param newCustomer
     */
    @Override
    public Customer addRootFamilyAndCategoryToNewCustomer(Customer newCustomer) {
        if (newCustomer == null) {
            throw new IllegalArgumentException("Customer is null");
        }

        if (newCustomer.getCategoryRoot() != null || newCustomer.getFamilyRoot() != null) {
            throw new IllegalArgumentException("One of the root is set, it should not be (family root or category root)");
        }

        Category categoryRoot = newRootCategory(newCustomer);
        Family familyRoot = newRootFamily(newCustomer);
        Family familyResult = familyRepository.save(familyRoot);
        Category categoryResult = categoryRepository.save(categoryRoot);


        familySearchRepository.save(familyResult);
        categorySearchRepository.save(categoryResult);
        newCustomer.setCategoryRoot(categoryResult.getId().intValue());
        newCustomer.setFamilyRoot(familyResult.getId().intValue());
        Customer savedCustomer = customerRepository.save(newCustomer);

        addDefaultAttributesInRootFamily(familyResult, savedCustomer);
        return savedCustomer;
    }
}
