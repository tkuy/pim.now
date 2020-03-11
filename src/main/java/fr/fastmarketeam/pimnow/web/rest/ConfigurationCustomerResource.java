package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.domain.ConfigurationCustomer;
import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.User;
import fr.fastmarketeam.pimnow.domain.UserExtra;
import fr.fastmarketeam.pimnow.repository.ConfigurationCustomerRepository;
import fr.fastmarketeam.pimnow.repository.CustomerRepository;
import fr.fastmarketeam.pimnow.repository.UserExtraRepository;
import fr.fastmarketeam.pimnow.repository.UserRepository;
import fr.fastmarketeam.pimnow.repository.search.ConfigurationCustomerSearchRepository;
import fr.fastmarketeam.pimnow.security.SecurityUtils;
import fr.fastmarketeam.pimnow.web.rest.errors.BadRequestAlertException;

import fr.fastmarketeam.pimnow.web.rest.errors.CustomerRequiredActionException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link fr.fastmarketeam.pimnow.domain.ConfigurationCustomer}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ConfigurationCustomerResource {

    private final Logger log = LoggerFactory.getLogger(ConfigurationCustomerResource.class);

    private static final String ENTITY_NAME = "configurationCustomer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConfigurationCustomerRepository configurationCustomerRepository;

    private final ConfigurationCustomerSearchRepository configurationCustomerSearchRepository;

    private final CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserExtraRepository userExtraRepository;

    public ConfigurationCustomerResource(ConfigurationCustomerRepository configurationCustomerRepository, ConfigurationCustomerSearchRepository configurationCustomerSearchRepository, CustomerRepository customerRepository) {
        this.configurationCustomerRepository = configurationCustomerRepository;
        this.configurationCustomerSearchRepository = configurationCustomerSearchRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * {@code PUT  /configuration-customers} : Updates an existing configurationCustomer.
     *
     * @param configurationCustomer the configurationCustomer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated configurationCustomer,
     * or with status {@code 400 (Bad Request)} if the configurationCustomer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the configurationCustomer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/configuration-customers")
    @Transactional
    public ResponseEntity<ConfigurationCustomer> updateConfigurationCustomer(@Valid @RequestBody ConfigurationCustomer configurationCustomer) throws URISyntaxException {

        Customer customer = findCustomer() ;
        long id_customer = customer.getId() ;

        Optional<ConfigurationCustomer> configurationCustomerCheck =
            configurationCustomerRepository.findById(id_customer);

        ConfigurationCustomer result = null ;

        if(configurationCustomerCheck.isPresent()){
            configurationCustomerCheck.get().setApiKeyPrestashop(configurationCustomer.getApiKeyPrestashop()) ;
            configurationCustomerCheck.get().setUrlPrestashop(configurationCustomer.getUrlPrestashop()) ;
            result = configurationCustomerRepository.save(configurationCustomerCheck.get());

            configurationCustomerSearchRepository.save(result);

            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, configurationCustomer.getId().toString()))
                .body(result);

        }   else    {
            configurationCustomer.setCustomer(customer);
            result = configurationCustomerRepository.save(configurationCustomer);

            configurationCustomerSearchRepository.save(result);

            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, configurationCustomer.getId().toString()))
                .body(result);
        }

    }

    /**
     * {@code GET  /configuration-customers} : get all the configurationCustomers.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of configurationCustomers in body.
     */
    @GetMapping("/configuration-customers")
    @Transactional(readOnly = true)
    public ConfigurationCustomer getAllConfigurationCustomers() {
        Optional<ConfigurationCustomer> configurationCustomer =
            configurationCustomerRepository.findById(findCustomer().getId());

        if(configurationCustomer.isPresent())
            return configurationCustomer.get() ;
        else
            return null ;
    }

    public Customer findCustomer() {
        Optional<User> user = findUser();
        Optional<UserExtra> userExtra = user.flatMap(u -> userExtraRepository.findById(u.getId()));
        if(userExtra.isPresent() && userExtra.get().getCustomer()!=null) {
            return userExtra.get().getCustomer();
        }
        throw new CustomerRequiredActionException();
    }

    public Optional<User> findUser() {
        return SecurityUtils.getCurrentUserLogin().flatMap(login -> userRepository.findOneByLogin(login));
    }

}
