package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.Category;
import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.repository.CategoryRepository;
import fr.fastmarketeam.pimnow.repository.search.CategorySearchRepository;
import fr.fastmarketeam.pimnow.security.AuthoritiesConstants;
import fr.fastmarketeam.pimnow.security.SecurityUtils;
import fr.fastmarketeam.pimnow.service.CategoryService;
import fr.fastmarketeam.pimnow.service.util.UserCustomerUtil;
import fr.fastmarketeam.pimnow.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.Category}.
 */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\") or hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
public class CategoryResource {

    private final Logger log = LoggerFactory.getLogger(CategoryResource.class);

    private static final String ENTITY_NAME = "category";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CategoryRepository categoryRepository;

    private final CategorySearchRepository categorySearchRepository;



    @Autowired
    private UserCustomerUtil userCustomerUtil ;

    private final CategoryService categoryService ;

    public CategoryResource(CategoryRepository categoryRepository, CategorySearchRepository categorySearchRepository, CategoryService categoryService) {
        this.categoryRepository = categoryRepository;
        this.categorySearchRepository = categorySearchRepository;
        this.categoryService = categoryService;
        this.userCustomerUtil = userCustomerUtil;
    }



    /**
     * {@code POST  /categories} : Create a new category.
     *
     * @param category the category to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new category, or with status {@code 400 (Bad Request)} if the category has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/categories")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) throws URISyntaxException {
        log.debug("REST request to save Category : {}", category);
        if (category.getId() != null) {
            throw new BadRequestAlertException("A new category cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Customer customer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin()) ;
        if(categoryRepository.findByIdFAndDeletedAndCustomerId(category.getIdF(), false, customer.getId()).isPresent()){
            throw new BadRequestAlertException("Cannot create a mapping with an already existing idf", ENTITY_NAME, "alreadyexistingcategory");
        }
        Category result = categoryService.createCategory(category, customer) ;
        categorySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getNom()))
            .body(result);
    }

    /**
     * {@code PUT  /categories} : Updates an existing category.
     *
     * @param category the category to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated category,
     * or with status {@code 400 (Bad Request)} if the category is not valid,
     * or with status {@code 500 (Internal Server Error)} if the category couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/categories")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
    public ResponseEntity<Category> updateCategory(@Valid @RequestBody Category category) throws URISyntaxException {
        log.debug("REST request to update Category : {}", category);

        if(!userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin()).getId().equals(categoryRepository.findById(category.getId()).get().getCustomer().getId())){
            throw new BadRequestAlertException("No right to edit this category", ENTITY_NAME, "notowner");
        }

        if (category.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        Category categoryEdit = categoryRepository.findById(category.getId()).get() ;

        categoryEdit.setNom(category.getNom());

        Category result = categoryRepository.save(categoryEdit);
        categorySearchRepository.save(categoryEdit);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, category.getNom()))
            .body(result);
    }

    /**
     * {@code GET  /categories} : get all the categories.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of categories in body.
     */
    @GetMapping("/categories")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\") or hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
    public List<Category> getAllCategories() {
        log.debug("REST request to get all Categories");
        Customer customer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        return categoryRepository.findAllByCustomerAndDeletedIsFalse(customer);

    }

    /**
     * {@code GET  /categories/:id} : get the "id" category.
     *
     * @param id the id of the category to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the category, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        log.debug("REST request to get Category : {}", id);
        Optional<Category> category = categoryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(category);
    }

    /**
     * {@code DELETE  /categories/:id} : delete the "id" category.
     *
     * @param id the id of the category to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.debug("REST request to delete Category : {}", id);
        if (categoryService.deleteCategoryById(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }



    @GetMapping("/categories/root")
    public ResponseEntity<Category> getCategoryRoot() {
        log.debug("REST request to get the family root");
        Long id = Long.valueOf(userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin()).getCategoryRoot());
        Optional<Category> optionalCategory = categoryRepository.findByIdAndCustomerWithEagerRelationship(id);
        return ResponseUtil.wrapOrNotFound(optionalCategory);
    }

    @GetMapping("/categories/predecessor/{id}")
    public long getCategoryPredecessor(@PathVariable Long id) {
        log.debug("REST request to get Category Predecessor : {}", id);
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isPresent()){
            if(category.get().getPredecessor() != null)
                return category.get().getPredecessor().getId() ;
        }
        return 0 ;
    }

}
