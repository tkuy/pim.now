package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.User;
import fr.fastmarketeam.pimnow.repository.CustomerRepository;
import fr.fastmarketeam.pimnow.repository.UserExtraRepository;
import fr.fastmarketeam.pimnow.repository.UserRepository;
import fr.fastmarketeam.pimnow.repository.search.CustomerSearchRepository;
import fr.fastmarketeam.pimnow.service.CustomerService;
import fr.fastmarketeam.pimnow.service.FileUploadService;
import fr.fastmarketeam.pimnow.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.Customer}.
 */
@RestController
@RequestMapping("/api")
public class CustomerResource {

    private final Logger log = LoggerFactory.getLogger(CustomerResource.class);

    private static final String ENTITY_NAME = "customer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private CustomerService customerService;

    private final CustomerRepository customerRepository;

    private final CustomerSearchRepository customerSearchRepository;

    private final FileUploadService fileUploadService;

    private final UserExtraRepository userExtraRepository;

    private final UserRepository userRepository;

    public CustomerResource(CustomerRepository customerRepository, CustomerSearchRepository customerSearchRepository, CustomerService customerService, FileUploadService fileUploadService, UserExtraRepository userExtraRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.customerSearchRepository = customerSearchRepository;
        this.customerService = customerService;
        this.fileUploadService = fileUploadService;
        this.userExtraRepository = userExtraRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /customers} : Create a new customer.
     *
     * @param customer the customer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customer, or with status {@code 400 (Bad Request)} if the customer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) throws URISyntaxException {
        log.debug("REST request to save Customer : {}", customer);
        if (customer.getId() != null) {
            throw new BadRequestAlertException("A new customer cannot already have an ID", ENTITY_NAME, "idexists");
        }

        Customer result = customerRepository.save(customer);

        result = customerService.addRootFamilyAndCategoryToNewCustomer(result);



        customerSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/customers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getName()))
            .body(result);
    }

    /**
     * {@code PUT  /customers} : Updates an existing customer.
     *
     * @param customer the customer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customer,
     * or with status {@code 400 (Bad Request)} if the customer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/customers")
    public ResponseEntity<Customer> updateCustomer(@Valid @RequestBody Customer customer) throws URISyntaxException {
        log.debug("REST request to update Customer : {}", customer);
        if (customer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Customer result = customerRepository.save(customer);
        customerSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customer.getName()))
            .body(result);
    }

    /**
     * {@code GET  /customers} : get all the customers.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customers in body.
     */
    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers(Pageable pageable) {
        log.debug("REST request to get a page of Customers");
        Page<Customer> page = customerRepository.findAllByIsDeleted(pageable, false);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /customers/:id} : get the "id" customer.
     *
     * @param id the id of the customer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        log.debug("REST request to get Customer : {}", id);
        Optional<Customer> customer = customerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(customer);
    }

    /**
     * {@code DELETE  /customers/:id} : deactivate the "id" customer and all users linked to it.
     *
     * @param id the id of the customer to deactivate.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.debug("REST request to deactivate Customer : {}", id);
        Optional<Customer> customer = customerRepository.findById(id);
        if(!customer.isPresent()){
            throw new BadRequestAlertException("Deleting a non existing customer", ENTITY_NAME, "nonexistingcustomer");
        }
        customer.get().setIsDeleted(true);
        List<User> users = userExtraRepository.findAllByCustomerIdAndUserActivated(customer.get().getId(), true).stream().map(userExtra -> {
            userExtra.getUser().setActivated(false);
            return userExtra.getUser();
        }).collect(Collectors.toList());
        userRepository.saveAll(users);
        customerRepository.save(customer.get());
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, customer.get().getName())).build();
    }

    /**
     * {@code SEARCH  /_search/customers?query=:query} : search for the customer corresponding
     * to the query.
     *
     * @param query the query of the customer search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/customers")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Customers for query {}", query);
        Page<Customer> page = customerSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("/logo")
    public void logo(@RequestParam("file") MultipartFile multipartFile) {
        try {
            fileUploadService.transferFile(multipartFile);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
