package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.PimnowApp;
import fr.fastmarketeam.pimnow.domain.ConfigurationCustomer;
import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.repository.ConfigurationCustomerRepository;
import fr.fastmarketeam.pimnow.repository.CustomerRepository;
import fr.fastmarketeam.pimnow.repository.search.ConfigurationCustomerSearchRepository;
import fr.fastmarketeam.pimnow.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static fr.fastmarketeam.pimnow.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ConfigurationCustomerResource} REST controller.
 */
@SpringBootTest(classes = PimnowApp.class)
public class ConfigurationCustomerResourceIT {

    private static final String DEFAULT_URL_PRESTASHOP = "AAAAAAAAAA";
    private static final String UPDATED_URL_PRESTASHOP = "BBBBBBBBBB";

    private static final String DEFAULT_API_KEY_PRESTASHOP = "AAAAAAAAAA";
    private static final String UPDATED_API_KEY_PRESTASHOP = "BBBBBBBBBB";

    @Autowired
    private ConfigurationCustomerRepository configurationCustomerRepository;
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * This repository is mocked in the fr.fastmarketeam.pimnow.repository.search test package.
     *
     * @see fr.fastmarketeam.pimnow.repository.search.ConfigurationCustomerSearchRepositoryMockConfiguration
     */
    @Autowired
    private ConfigurationCustomerSearchRepository mockConfigurationCustomerSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restConfigurationCustomerMockMvc;

    private ConfigurationCustomer configurationCustomer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ConfigurationCustomerResource configurationCustomerResource = new ConfigurationCustomerResource(configurationCustomerRepository, mockConfigurationCustomerSearchRepository, customerRepository);
        this.restConfigurationCustomerMockMvc = MockMvcBuilders.standaloneSetup(configurationCustomerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ConfigurationCustomer createEntity(EntityManager em) {
        ConfigurationCustomer configurationCustomer = new ConfigurationCustomer()
            .urlPrestashop(DEFAULT_URL_PRESTASHOP)
            .apiKeyPrestashop(DEFAULT_API_KEY_PRESTASHOP);
        // Add required entity
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            customer = CustomerResourceIT.createEntity(em);
            em.persist(customer);
            em.flush();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        configurationCustomer.setCustomer(customer);
        return configurationCustomer;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ConfigurationCustomer createUpdatedEntity(EntityManager em) {
        ConfigurationCustomer configurationCustomer = new ConfigurationCustomer()
            .urlPrestashop(UPDATED_URL_PRESTASHOP)
            .apiKeyPrestashop(UPDATED_API_KEY_PRESTASHOP);
        // Add required entity
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            customer = CustomerResourceIT.createUpdatedEntity(em);
            em.persist(customer);
            em.flush();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        configurationCustomer.setCustomer(customer);
        return configurationCustomer;
    }

    @BeforeEach
    public void initTest() {
        configurationCustomer = createEntity(em);
    }

    @Transactional
    public void createConfigurationCustomer() throws Exception {
        int databaseSizeBeforeCreate = configurationCustomerRepository.findAll().size();

        // Create the ConfigurationCustomer
        restConfigurationCustomerMockMvc.perform(put("/api/configuration-customers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(configurationCustomer)))
            .andExpect(status().isCreated());

        // Validate the ConfigurationCustomer in the database
        List<ConfigurationCustomer> configurationCustomerList = configurationCustomerRepository.findAll();
        assertThat(configurationCustomerList).hasSize(databaseSizeBeforeCreate + 1);
        ConfigurationCustomer testConfigurationCustomer = configurationCustomerList.get(configurationCustomerList.size() - 1);
        assertThat(testConfigurationCustomer.getUrlPrestashop()).isEqualTo(DEFAULT_URL_PRESTASHOP);
        assertThat(testConfigurationCustomer.getApiKeyPrestashop()).isEqualTo(DEFAULT_API_KEY_PRESTASHOP);

        // Validate the id for MapsId, the ids must be same
        assertThat(testConfigurationCustomer.getId()).isEqualTo(testConfigurationCustomer.getCustomer().getId());

        // Validate the ConfigurationCustomer in Elasticsearch
        verify(mockConfigurationCustomerSearchRepository, times(1)).save(testConfigurationCustomer);
    }

    @Transactional
    public void updateConfigurationCustomerMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        configurationCustomerRepository.saveAndFlush(configurationCustomer);
        int databaseSizeBeforeCreate = configurationCustomerRepository.findAll().size();

        // Add a new parent entity
        Customer customer = CustomerResourceIT.createUpdatedEntity(em);
        em.persist(customer);
        em.flush();

        // Load the configurationCustomer
        ConfigurationCustomer updatedConfigurationCustomer = configurationCustomerRepository.findById(configurationCustomer.getId()).get();
        // Disconnect from session so that the updates on updatedConfigurationCustomer are not directly saved in db
        em.detach(updatedConfigurationCustomer);

        // Update the Customer with new association value
        updatedConfigurationCustomer.setCustomer(customer);

        // Update the entity
        restConfigurationCustomerMockMvc.perform(put("/api/configuration-customers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedConfigurationCustomer)))
            .andExpect(status().isOk());

        // Validate the ConfigurationCustomer in the database
        List<ConfigurationCustomer> configurationCustomerList = configurationCustomerRepository.findAll();
        assertThat(configurationCustomerList).hasSize(databaseSizeBeforeCreate);
        ConfigurationCustomer testConfigurationCustomer = configurationCustomerList.get(configurationCustomerList.size() - 1);

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testConfigurationCustomer.getId()).isEqualTo(testConfigurationCustomer.getCustomer().getId());

        // Validate the ConfigurationCustomer in Elasticsearch
        verify(mockConfigurationCustomerSearchRepository, times(1)).save(configurationCustomer);
    }

    @Transactional
    public void checkUrlPrestashopIsRequired() throws Exception {
        int databaseSizeBeforeTest = configurationCustomerRepository.findAll().size();
        // set the field null
        configurationCustomer.setUrlPrestashop(null);

        // Create the ConfigurationCustomer, which fails.

        restConfigurationCustomerMockMvc.perform(post("/api/configuration-customers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(configurationCustomer)))
            .andExpect(status().isBadRequest());

        List<ConfigurationCustomer> configurationCustomerList = configurationCustomerRepository.findAll();
        assertThat(configurationCustomerList).hasSize(databaseSizeBeforeTest);
    }

    @Transactional
    public void checkApiKeyPrestashopIsRequired() throws Exception {
        int databaseSizeBeforeTest = configurationCustomerRepository.findAll().size();
        // set the field null
        configurationCustomer.setApiKeyPrestashop(null);

        // Create the ConfigurationCustomer, which fails.

        restConfigurationCustomerMockMvc.perform(post("/api/configuration-customers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(configurationCustomer)))
            .andExpect(status().isBadRequest());

        List<ConfigurationCustomer> configurationCustomerList = configurationCustomerRepository.findAll();
        assertThat(configurationCustomerList).hasSize(databaseSizeBeforeTest);
    }

    @Transactional
    public void getAllConfigurationCustomers() throws Exception {
        // Initialize the database
        configurationCustomerRepository.saveAndFlush(configurationCustomer);

        // Get all the configurationCustomerList
        restConfigurationCustomerMockMvc.perform(get("/api/configuration-customers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(configurationCustomer.getId().intValue())))
            .andExpect(jsonPath("$.[*].urlPrestashop").value(hasItem(DEFAULT_URL_PRESTASHOP.toString())))
            .andExpect(jsonPath("$.[*].apiKeyPrestashop").value(hasItem(DEFAULT_API_KEY_PRESTASHOP.toString())));
    }

    @Transactional
    public void getConfigurationCustomer() throws Exception {
        // Initialize the database
        configurationCustomerRepository.saveAndFlush(configurationCustomer);

        // Get the configurationCustomer
        restConfigurationCustomerMockMvc.perform(get("/api/configuration-customers", configurationCustomer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(configurationCustomer.getId().intValue()))
            .andExpect(jsonPath("$.urlPrestashop").value(DEFAULT_URL_PRESTASHOP.toString()))
            .andExpect(jsonPath("$.apiKeyPrestashop").value(DEFAULT_API_KEY_PRESTASHOP.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingConfigurationCustomer() throws Exception {
        // Get the configurationCustomer
        restConfigurationCustomerMockMvc.perform(get("/api/configuration-customers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Transactional
    public void updateConfigurationCustomer() throws Exception {
        // Initialize the database
        configurationCustomerRepository.saveAndFlush(configurationCustomer);

        int databaseSizeBeforeUpdate = configurationCustomerRepository.findAll().size();

        // Update the configurationCustomer
        ConfigurationCustomer updatedConfigurationCustomer = configurationCustomerRepository.findById(configurationCustomer.getId()).get();
        // Disconnect from session so that the updates on updatedConfigurationCustomer are not directly saved in db
        em.detach(updatedConfigurationCustomer);
        updatedConfigurationCustomer
            .urlPrestashop(UPDATED_URL_PRESTASHOP)
            .apiKeyPrestashop(UPDATED_API_KEY_PRESTASHOP);

        restConfigurationCustomerMockMvc.perform(put("/api/configuration-customers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedConfigurationCustomer)))
            .andExpect(status().isOk());

        // Validate the ConfigurationCustomer in the database
        List<ConfigurationCustomer> configurationCustomerList = configurationCustomerRepository.findAll();
        assertThat(configurationCustomerList).hasSize(databaseSizeBeforeUpdate);
        ConfigurationCustomer testConfigurationCustomer = configurationCustomerList.get(configurationCustomerList.size() - 1);
        assertThat(testConfigurationCustomer.getUrlPrestashop()).isEqualTo(UPDATED_URL_PRESTASHOP);
        assertThat(testConfigurationCustomer.getApiKeyPrestashop()).isEqualTo(UPDATED_API_KEY_PRESTASHOP);

        // Validate the ConfigurationCustomer in Elasticsearch
        verify(mockConfigurationCustomerSearchRepository, times(1)).save(testConfigurationCustomer);
    }

    @Transactional
    public void updateNonExistingConfigurationCustomer() throws Exception {
        int databaseSizeBeforeUpdate = configurationCustomerRepository.findAll().size();

        // Create the ConfigurationCustomer

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConfigurationCustomerMockMvc.perform(put("/api/configuration-customers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(configurationCustomer)))
            .andExpect(status().isBadRequest());

        // Validate the ConfigurationCustomer in the database
        List<ConfigurationCustomer> configurationCustomerList = configurationCustomerRepository.findAll();
        assertThat(configurationCustomerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ConfigurationCustomer in Elasticsearch
        verify(mockConfigurationCustomerSearchRepository, times(0)).save(configurationCustomer);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ConfigurationCustomer.class);
        ConfigurationCustomer configurationCustomer1 = new ConfigurationCustomer();
        configurationCustomer1.setId(1L);
        ConfigurationCustomer configurationCustomer2 = new ConfigurationCustomer();
        configurationCustomer2.setId(configurationCustomer1.getId());
        assertThat(configurationCustomer1).isEqualTo(configurationCustomer2);
        configurationCustomer2.setId(2L);
        assertThat(configurationCustomer1).isNotEqualTo(configurationCustomer2);
        configurationCustomer1.setId(null);
        assertThat(configurationCustomer1).isNotEqualTo(configurationCustomer2);
    }
}
