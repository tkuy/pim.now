package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.*;
import fr.fastmarketeam.pimnow.repository.ProductRepository;
import fr.fastmarketeam.pimnow.repository.search.ProductSearchRepository;
import fr.fastmarketeam.pimnow.security.AuthoritiesConstants;
import fr.fastmarketeam.pimnow.security.SecurityUtils;
import fr.fastmarketeam.pimnow.service.ProductService;
import fr.fastmarketeam.pimnow.service.util.UserCustomerUtil;
import fr.fastmarketeam.pimnow.web.rest.errors.BadRequestAlertException;
import fr.fastmarketeam.pimnow.web.rest.vm.ProductVM;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.Product}.
 */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\") or hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
public class ProductResource {

    private final Logger log = LoggerFactory.getLogger(ProductResource.class);

    private static final String ENTITY_NAME = "product";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductRepository productRepository;

    private final ProductSearchRepository productSearchRepository;

    private final ProductService productService;

    private final UserCustomerUtil userCustomerUtil;


    public ProductResource(ProductRepository productRepository, ProductSearchRepository productSearchRepository, ProductService productService, UserCustomerUtil userCustomerUtil) {
        this.productRepository = productRepository;
        this.productSearchRepository = productSearchRepository;
        this.productService = productService;
        this.userCustomerUtil = userCustomerUtil;
    }

    /**
     * {@code POST  /products} : Create a new product.
     *
     * @param productVM the product to create with all the informations to link it with workflows, categories, family etc....
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new product, or with status {@code 400 (Bad Request)} if the product has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping(value = "/products")
    public ResponseEntity<Product> createProduct(@RequestPart @Valid ProductVM productVM, @RequestPart(value = "files") MultipartFile[] files) throws URISyntaxException, IOException {
        log.debug("REST request to save Product : {}", productVM);

        if (productVM.getId() != null) {
            throw new BadRequestAlertException("A new product cannot already have an ID", ENTITY_NAME, "idexists");
        }
        HashMap<String, MultipartFile> multipartFiles = new HashMap<>();
        for (MultipartFile file : files) {
            multipartFiles.put(file.getOriginalFilename(), file);
        }
        Product result = productService.createProduct(
            productVM.toProduct(),
            productVM.getAttributValues(),
            userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin()),
            productVM.getFamily(),
            productVM.getWorkflows(),
            productVM.getCategories(),
            multipartFiles);

        return ResponseEntity.created(new URI("/api/products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getNom()))
            .body(result);
    }

    /**
     * {@code PUT  /products} : Updates an existing product.
     *
     * @param productVM the product to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated product,
     * or with status {@code 400 (Bad Request)} if the product is not valid,
     * or with status {@code 500 (Internal Server Error)} if the product couldn't be updated.
     */
    @PutMapping("/products")
    public ResponseEntity<Product> updateProduct(@RequestPart @Valid ProductVM productVM, @RequestPart(value = "files") MultipartFile[] files) throws IOException {
        log.debug("REST request to update ProductVM : {}", productVM);
        if (productVM.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        HashMap<String, MultipartFile> multipartFiles = new HashMap<>();
        for (MultipartFile file : files) {
            multipartFiles.put(file.getOriginalFilename(), file);
        }
        Product result = productService.updateProduct(productVM.toProduct(), productVM.getAttributValues(), productVM.getCategories(), multipartFiles);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getNom()))
            .body(result);
    }

    /**
     * {@code GET  /products} : get all the products.
     *

     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in body.
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(Pageable pageable, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get a page of Products");
        Page<Product> page;
        Customer customer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        if (eagerload) {
            page = productRepository.findAllByCustomerIdWithEagerRelationships(pageable, customer.getId());
        } else {
            page = productRepository.findAllByCustomerIdAndIsDeletedIsFalse(pageable, customer.getId());
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /products/selectAll} : get all the products.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in body.
     */
    @GetMapping("/products/selectAll")
    public List<Product> selectAllProducts() {
        log.debug("REST request to get all products");
        Customer customer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        return productRepository.findAllByCustomerIdAndIsDeleted(customer.getId(), false);
    }

    /**
     * {@code GET  /products/:id} : get the "id" product.
     *
     * @param id the id of the product to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the product, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductVM> getProduct(@PathVariable Long id) {
        log.debug("REST request to get Product : {}", id);
        Customer customer = userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        Optional<Product> product = productRepository.findOneWithEagerRelationships(id, customer.getId());
        List<AttributValue> attributValues = productService.findAttributValuesByProductId(id, customer.getId());
        Optional<ProductVM> productVM = product.map(p -> {
            ProductVM vm = new ProductVM().toProductVM(p);
            vm.setAttributValues(new HashSet<>(attributValues));
            return vm;
        });
        return ResponseUtil.wrapOrNotFound(productVM);
    }

    /**
     * {@code DELETE  /products/:id} : delete the "id" product.
     *
     * @param id the id of the product to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN_FONC + "\")")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.debug("REST request to delete Product : {}", id);
        Customer customer =  userCustomerUtil.findCustomer(SecurityUtils.getCurrentUserLogin());
        Optional<Product> optP = productRepository.findById(id);
        if (!optP.isPresent()) {
            Optional<User> optU = userCustomerUtil.findUser(SecurityUtils.getCurrentUserLogin());
            if (!optU.isPresent()) {
                throw new IllegalStateException("Could not find the user");
            }
            User u = optU.get();
            log.warn("User " + u.getId() + "tried to delete non existing product " + id);
            return ResponseEntity.notFound().build();
        }
        Product p = optP.get();
        if (p.getCustomer() == null || !p.getCustomer().getId().equals(customer.getId())) {
            return ResponseEntity.notFound().build();
        }
        p.isDeleted(true);
        productRepository.save(p);
        productSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, p.getNom())).build();
    }

    /**
     * {@code SEARCH  /_search/products?query=:query} : search for the product corresponding
     * to the query.
     *
     * @param query the query of the product search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/products")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query, @PageableDefault(value = 10000) Pageable pageable) {
        log.debug("REST request to search for a page of Products for query {}", query);
        Page<Product> page = productService.getSearchProductPage(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
