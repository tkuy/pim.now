package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.PimnowApp;
import fr.fastmarketeam.pimnow.domain.Customer;
import fr.fastmarketeam.pimnow.domain.Family;
import fr.fastmarketeam.pimnow.domain.Product;
import fr.fastmarketeam.pimnow.domain.ProductsWithAttributes;
import fr.fastmarketeam.pimnow.repository.CustomerRepository;
import fr.fastmarketeam.pimnow.repository.ProductRepository;
import fr.fastmarketeam.pimnow.repository.UserExtraRepository;
import fr.fastmarketeam.pimnow.repository.UserRepository;
import fr.fastmarketeam.pimnow.repository.search.ProductSearchRepository;
import fr.fastmarketeam.pimnow.service.ProductService;
import fr.fastmarketeam.pimnow.service.util.UserCustomerUtil;
import fr.fastmarketeam.pimnow.web.rest.errors.ExceptionTranslator;
import fr.fastmarketeam.pimnow.web.rest.vm.ProductVM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.*;

import static fr.fastmarketeam.pimnow.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ProductResource} REST controller.
 */
@SpringBootTest(classes = PimnowApp.class)
public class ProductResourceIT {

    private static final String DEFAULT_ID_F = "AAAAAAAAAA";
    private static final String UPDATED_ID_F = "BBBBBBBBBB";

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private ProductRepository productRepository;

    @Mock
    private ProductRepository productRepositoryMock;

    @Mock
    private ProductService productService;

    @Mock
    private UserExtraRepository userExtraRepository;

    @Mock
    private UserRepository userRepository;

    /**
     * This repository is mocked in the fr.fastmarketeam.pimnow.repository.search test package.
     *
     * @see fr.fastmarketeam.pimnow.repository.search.ProductSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProductSearchRepository mockProductSearchRepository;

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

    private MockMvc restProductMockMvc;

    @Autowired
    private UserCustomerUtil userCustomerUtil;

    private Product product;
    private Customer customer;
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProductResource productResource = new ProductResource(productRepository, mockProductSearchRepository, productService, userCustomerUtil);
        this.restProductMockMvc = MockMvcBuilders.standaloneSetup(productResource)
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
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .idF(DEFAULT_ID_F)
            .nom(DEFAULT_NOM)
            .description(DEFAULT_DESCRIPTION);
        return product;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity(EntityManager em) {
        Product product = new Product()
            .idF(UPDATED_ID_F)
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION);
        return product;
    }

    public static ProductVM createProductVM(Product product) {
        Family family = new Family();
        family.setId(product.getId());
        family.setIdF(DEFAULT_ID_F);
        family.setNom(DEFAULT_NOM);
        ProductVM productVM = new ProductVM();
        productVM.setId(product.getId());
        productVM.setIdF(product.getIdF());
        productVM.setDescription(product.getDescription());
        productVM.setNom(product.getNom());
        productVM.setAttributValues(new HashSet<>());
        productVM.setCategories(new HashSet<>());
        productVM.setWorkflows(new HashSet<>());
        productVM.setFamily(family);
        return productVM;
    }
    public static ProductVM createProductVM() {
        Family family = new Family();
        family.setId(1L);
        ProductVM productVM = new ProductVM();
        productVM.setIdF(DEFAULT_ID_F);
        productVM.setDescription(DEFAULT_DESCRIPTION);
        productVM.setNom(DEFAULT_NOM);
        productVM.setAttributValues(new HashSet<>());
        productVM.setCategories(new HashSet<>());
        productVM.setWorkflows(new HashSet<>());
        productVM.setFamily(family);
//        productVM.setFiles(new HashSet<>());
        return productVM;
    }

    @BeforeEach
    public void initTest() {
        product = createEntity(em);
        customer = new Customer().idF("idf").name("name");
    }

//    @Test
//    @Transactional
    public void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();
        ProductVM productVM = createProductVM();
        // Create the Product
        restProductMockMvc.perform(post("/api/products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productVM)))
            .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getIdF()).isEqualTo(DEFAULT_ID_F);
        assertThat(testProduct.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        ProductsWithAttributes productsWithAttributes = new ProductsWithAttributes().setProduct(testProduct).setAttributValues(new HashSet<>());
        productsWithAttributes.initId();
        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(1)).save(productsWithAttributes);
    }

    @Test
    @Transactional
    public void createProductWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // Create the Product with an existing ID
        product.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc.perform(post("/api/products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);

        // Validate the Product in Elasticsearch
        ProductsWithAttributes productsWithAttributes = new ProductsWithAttributes().setProduct(product).setAttributValues(new HashSet<>());
        productsWithAttributes.initId();
        verify(mockProductSearchRepository, times(0)).save(productsWithAttributes);
    }


    @Test
    @Transactional
    public void checkIdFIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setIdF(null);

        // Create the Product, which fails.

        restProductMockMvc.perform(post("/api/products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setNom(null);

        // Create the Product, which fails.

        restProductMockMvc.perform(post("/api/products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

//    @Test
//    @Transactional
    public void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc.perform(get("/api/products?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].idF").value(hasItem(DEFAULT_ID_F.toString())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @SuppressWarnings({"unchecked"})
    public void getAllProductsWithEagerRelationshipsIsEnabled() throws Exception {
        ProductResource productResource = new ProductResource(productRepositoryMock, mockProductSearchRepository, productService, userCustomerUtil);
        when(productRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
        MockMvc restProductMockMvc = MockMvcBuilders.standaloneSetup(productResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restProductMockMvc.perform(get("/api/products?eagerload=true"))
        .andExpect(status().isOk());

        verify(productRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllProductsWithEagerRelationshipsIsNotEnabled() throws Exception {
        ProductResource productResource = new ProductResource(productRepositoryMock, mockProductSearchRepository, productService, userCustomerUtil);
            when(productRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restProductMockMvc = MockMvcBuilders.standaloneSetup(productResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restProductMockMvc.perform(get("/api/products?eagerload=true"))
        .andExpect(status().isOk());

            verify(productRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

//    @Test
//    @Transactional
    public void getProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.idF").value(DEFAULT_ID_F.toString()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

//    @Test
//    @Transactional
    public void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get("/api/products/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

//    @Test
//    @Transactional
    public void updateProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).get();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .idF(UPDATED_ID_F)
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION);
        product.setId(updatedProduct.getId());
        ProductVM productVM = createProductVM(product);
        restProductMockMvc.perform(put("/api/products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productVM)))
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getIdF()).isEqualTo(UPDATED_ID_F);
        assertThat(testProduct.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        ProductsWithAttributes productsWithAttributes = new ProductsWithAttributes().setProduct(testProduct).setAttributValues(new HashSet<>());
        productsWithAttributes.initId();
        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(1)).save(productsWithAttributes);
    }

    @Test
    @Transactional
    public void updateNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Create the Product

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc.perform(put("/api/products")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(product)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);

        ProductsWithAttributes productsWithAttributes = new ProductsWithAttributes().setProduct(product).setAttributValues(new HashSet<>());
        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(0)).save(productsWithAttributes);
    }

    //@Test
    @Transactional
    public void deleteProduct() throws Exception {
        customer = customerRepository.save(customer);
        product.setCustomer(customer);
        // Initialize the database
        productRepository.saveAndFlush(product);
        int databaseSizeBeforeDelete = productRepository.findAll().size();
        when(userCustomerUtil.findCustomer(Optional.of("user@localhost"))).thenReturn(customer);
        // Delete the product
        restProductMockMvc.perform(delete("/api/products/{id}", product.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Product in Elasticsearch
        verify(mockProductSearchRepository, times(1)).deleteById(product.getId());
    }

    //@Test
    @Transactional
    public void searchProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);
        when(productService.getSearchProductPage(product.getNom(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(product), PageRequest.of(0, 1), 1));
        // Search the product
        restProductMockMvc.perform(get("/api/_search/products?query=" + product.getNom()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].idF").value(hasItem(DEFAULT_ID_F)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);
        product2.setId(2L);
        assertThat(product1).isNotEqualTo(product2);
        product1.setId(null);
        assertThat(product1).isNotEqualTo(product2);
    }
}
