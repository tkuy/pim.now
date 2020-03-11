package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.PimnowApp;
import fr.fastmarketeam.pimnow.domain.PrestashopProduct;
import fr.fastmarketeam.pimnow.domain.Product;
import fr.fastmarketeam.pimnow.repository.PrestashopProductRepository;
import fr.fastmarketeam.pimnow.repository.search.PrestashopProductSearchRepository;
import fr.fastmarketeam.pimnow.service.PrestashopProductService;
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
 * Integration tests for the {@link PrestashopProductResource} REST controller.
 */
@SpringBootTest(classes = PimnowApp.class)
public class PrestashopProductResourceIT {

    private static final Long DEFAULT_PRESTASHOP_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRESTASHOP_PRODUCT_ID = 2L;
    private static final Long SMALLER_PRESTASHOP_PRODUCT_ID = 1L - 1L;

    @Autowired
    private PrestashopProductRepository prestashopProductRepository;

    @Autowired
    private PrestashopProductService prestashopProductService;

    /**
     * This repository is mocked in the fr.fastmarketeam.pimnow.repository.search test package.
     *
     * @see fr.fastmarketeam.pimnow.repository.search.PrestashopProductSearchRepositoryMockConfiguration
     */
    @Autowired
    private PrestashopProductSearchRepository mockPrestashopProductSearchRepository;

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

    private MockMvc restPrestashopProductMockMvc;

    private PrestashopProduct prestashopProduct;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PrestashopProductResource prestashopProductResource = new PrestashopProductResource(prestashopProductService);
        this.restPrestashopProductMockMvc = MockMvcBuilders.standaloneSetup(prestashopProductResource)
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
    public static PrestashopProduct createEntity(EntityManager em) {
        PrestashopProduct prestashopProduct = new PrestashopProduct()
            .prestashopProductId(DEFAULT_PRESTASHOP_PRODUCT_ID);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        prestashopProduct.setProductPim(product);
        return prestashopProduct;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PrestashopProduct createUpdatedEntity(EntityManager em) {
        PrestashopProduct prestashopProduct = new PrestashopProduct()
            .prestashopProductId(UPDATED_PRESTASHOP_PRODUCT_ID);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createUpdatedEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        prestashopProduct.setProductPim(product);
        return prestashopProduct;
    }

    @BeforeEach
    public void initTest() {
        prestashopProduct = createEntity(em);
    }

    @Test
    @Transactional
    public void createPrestashopProduct() throws Exception {
        int databaseSizeBeforeCreate = prestashopProductRepository.findAll().size();

        // Create the PrestashopProduct
        restPrestashopProductMockMvc.perform(post("/api/prestashop-products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(prestashopProduct)))
            .andExpect(status().isCreated());

        // Validate the PrestashopProduct in the database
        List<PrestashopProduct> prestashopProductList = prestashopProductRepository.findAll();
        assertThat(prestashopProductList).hasSize(databaseSizeBeforeCreate + 1);
        PrestashopProduct testPrestashopProduct = prestashopProductList.get(prestashopProductList.size() - 1);
        assertThat(testPrestashopProduct.getPrestashopProductId()).isEqualTo(DEFAULT_PRESTASHOP_PRODUCT_ID);

        // Validate the id for MapsId, the ids must be same
        assertThat(testPrestashopProduct.getId()).isEqualTo(testPrestashopProduct.getProductPim().getId());

        // Validate the PrestashopProduct in Elasticsearch
        verify(mockPrestashopProductSearchRepository, times(1)).save(testPrestashopProduct);
    }

    @Test
    @Transactional
    public void createPrestashopProductWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = prestashopProductRepository.findAll().size();

        // Create the PrestashopProduct with an existing ID
        prestashopProduct.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPrestashopProductMockMvc.perform(post("/api/prestashop-products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(prestashopProduct)))
            .andExpect(status().isBadRequest());

        // Validate the PrestashopProduct in the database
        List<PrestashopProduct> prestashopProductList = prestashopProductRepository.findAll();
        assertThat(prestashopProductList).hasSize(databaseSizeBeforeCreate);

        // Validate the PrestashopProduct in Elasticsearch
        verify(mockPrestashopProductSearchRepository, times(0)).save(prestashopProduct);
    }

    @Test
    @Transactional
    public void updatePrestashopProductMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        prestashopProductService.save(prestashopProduct);
        int databaseSizeBeforeCreate = prestashopProductRepository.findAll().size();

        // Add a new parent entity
        Product product = ProductResourceIT.createUpdatedEntity(em);
        em.persist(product);
        em.flush();

        // Load the prestashopProduct
        PrestashopProduct updatedPrestashopProduct = prestashopProductRepository.findById(prestashopProduct.getId()).get();
        // Disconnect from session so that the updates on updatedPrestashopProduct are not directly saved in db
        em.detach(updatedPrestashopProduct);

        // Update the Product with new association value
        updatedPrestashopProduct.setProductPim(product);

        // Update the entity
        restPrestashopProductMockMvc.perform(put("/api/prestashop-products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPrestashopProduct)))
            .andExpect(status().isOk());

        // Validate the PrestashopProduct in the database
        List<PrestashopProduct> prestashopProductList = prestashopProductRepository.findAll();
        assertThat(prestashopProductList).hasSize(databaseSizeBeforeCreate);
        PrestashopProduct testPrestashopProduct = prestashopProductList.get(prestashopProductList.size() - 1);

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testPrestashopProduct.getId()).isEqualTo(testPrestashopProduct.getProduct().getId());

        // Validate the PrestashopProduct in Elasticsearch
        verify(mockPrestashopProductSearchRepository, times(2)).save(prestashopProduct);
    }

    @Test
    @Transactional
    public void checkPrestashopProductIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = prestashopProductRepository.findAll().size();
        // set the field null
        prestashopProduct.setPrestashopProductId(null);

        // Create the PrestashopProduct, which fails.

        restPrestashopProductMockMvc.perform(post("/api/prestashop-products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(prestashopProduct)))
            .andExpect(status().isBadRequest());

        List<PrestashopProduct> prestashopProductList = prestashopProductRepository.findAll();
        assertThat(prestashopProductList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPrestashopProducts() throws Exception {
        // Initialize the database
        prestashopProductRepository.saveAndFlush(prestashopProduct);

        // Get all the prestashopProductList
        restPrestashopProductMockMvc.perform(get("/api/prestashop-products?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prestashopProduct.getId().intValue())))
            .andExpect(jsonPath("$.[*].prestashopProductId").value(hasItem(DEFAULT_PRESTASHOP_PRODUCT_ID.intValue())));
    }

    @Test
    @Transactional
    public void getPrestashopProduct() throws Exception {
        // Initialize the database
        prestashopProductRepository.saveAndFlush(prestashopProduct);

        // Get the prestashopProduct
        restPrestashopProductMockMvc.perform(get("/api/prestashop-products/{id}", prestashopProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(prestashopProduct.getId().intValue()))
            .andExpect(jsonPath("$.prestashopProductId").value(DEFAULT_PRESTASHOP_PRODUCT_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPrestashopProduct() throws Exception {
        // Get the prestashopProduct
        restPrestashopProductMockMvc.perform(get("/api/prestashop-products/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePrestashopProduct() throws Exception {
        // Initialize the database
        prestashopProductService.save(prestashopProduct);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockPrestashopProductSearchRepository);

        int databaseSizeBeforeUpdate = prestashopProductRepository.findAll().size();

        // Update the prestashopProduct
        PrestashopProduct updatedPrestashopProduct = prestashopProductRepository.findById(prestashopProduct.getId()).get();
        // Disconnect from session so that the updates on updatedPrestashopProduct are not directly saved in db
        em.detach(updatedPrestashopProduct);
        updatedPrestashopProduct
            .prestashopProductId(UPDATED_PRESTASHOP_PRODUCT_ID);

        restPrestashopProductMockMvc.perform(put("/api/prestashop-products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPrestashopProduct)))
            .andExpect(status().isOk());

        // Validate the PrestashopProduct in the database
        List<PrestashopProduct> prestashopProductList = prestashopProductRepository.findAll();
        assertThat(prestashopProductList).hasSize(databaseSizeBeforeUpdate);
        PrestashopProduct testPrestashopProduct = prestashopProductList.get(prestashopProductList.size() - 1);
        assertThat(testPrestashopProduct.getPrestashopProductId()).isEqualTo(UPDATED_PRESTASHOP_PRODUCT_ID);

        // Validate the PrestashopProduct in Elasticsearch
        verify(mockPrestashopProductSearchRepository, times(1)).save(testPrestashopProduct);
    }

    @Test
    @Transactional
    public void updateNonExistingPrestashopProduct() throws Exception {
        int databaseSizeBeforeUpdate = prestashopProductRepository.findAll().size();

        // Create the PrestashopProduct

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrestashopProductMockMvc.perform(put("/api/prestashop-products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(prestashopProduct)))
            .andExpect(status().isBadRequest());

        // Validate the PrestashopProduct in the database
        List<PrestashopProduct> prestashopProductList = prestashopProductRepository.findAll();
        assertThat(prestashopProductList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PrestashopProduct in Elasticsearch
        verify(mockPrestashopProductSearchRepository, times(0)).save(prestashopProduct);
    }

    @Test
    @Transactional
    public void deletePrestashopProduct() throws Exception {
        // Initialize the database
        prestashopProductService.save(prestashopProduct);

        int databaseSizeBeforeDelete = prestashopProductRepository.findAll().size();

        // Delete the prestashopProduct
        restPrestashopProductMockMvc.perform(delete("/api/prestashop-products/{id}", prestashopProduct.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PrestashopProduct> prestashopProductList = prestashopProductRepository.findAll();
        assertThat(prestashopProductList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PrestashopProduct in Elasticsearch
        verify(mockPrestashopProductSearchRepository, times(1)).deleteById(prestashopProduct.getId());
    }

    @Test
    @Transactional
    public void searchPrestashopProduct() throws Exception {
        // Initialize the database
        prestashopProductService.save(prestashopProduct);
        when(mockPrestashopProductSearchRepository.search(queryStringQuery("id:" + prestashopProduct.getId())))
            .thenReturn(Collections.singletonList(prestashopProduct));
        // Search the prestashopProduct
        restPrestashopProductMockMvc.perform(get("/api/_search/prestashop-products?query=id:" + prestashopProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prestashopProduct.getId().intValue())))
            .andExpect(jsonPath("$.[*].prestashopProductId").value(hasItem(DEFAULT_PRESTASHOP_PRODUCT_ID.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrestashopProduct.class);
        PrestashopProduct prestashopProduct1 = new PrestashopProduct();
        prestashopProduct1.setId(1L);
        PrestashopProduct prestashopProduct2 = new PrestashopProduct();
        prestashopProduct2.setId(prestashopProduct1.getId());
        assertThat(prestashopProduct1).isEqualTo(prestashopProduct2);
        prestashopProduct2.setId(2L);
        assertThat(prestashopProduct1).isNotEqualTo(prestashopProduct2);
        prestashopProduct1.setId(null);
        assertThat(prestashopProduct1).isNotEqualTo(prestashopProduct2);
    }
}
