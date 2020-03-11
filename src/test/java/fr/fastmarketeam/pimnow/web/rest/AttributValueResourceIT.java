package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.PimnowApp;
import fr.fastmarketeam.pimnow.domain.AttributValue;
import fr.fastmarketeam.pimnow.repository.AttributValueRepository;
import fr.fastmarketeam.pimnow.repository.search.AttributValueSearchRepository;
import fr.fastmarketeam.pimnow.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
 * Integration tests for the {@link AttributValueResource} REST controller.
 */
@SpringBootTest(classes = PimnowApp.class)
public class AttributValueResourceIT {

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    @Autowired
    private AttributValueRepository attributValueRepository;

    /**
     * This repository is mocked in the fr.fastmarketeam.pimnow.repository.search test package.
     *
     * @see fr.fastmarketeam.pimnow.repository.search.AttributValueSearchRepositoryMockConfiguration
     */
    @Autowired
    private AttributValueSearchRepository mockAttributValueSearchRepository;

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

    private MockMvc restAttributValueMockMvc;

    private AttributValue attributValue;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AttributValueResource attributValueResource = new AttributValueResource(attributValueRepository, mockAttributValueSearchRepository);
        this.restAttributValueMockMvc = MockMvcBuilders.standaloneSetup(attributValueResource)
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
    public static AttributValue createEntity(EntityManager em) {
        AttributValue attributValue = new AttributValue()
            .value(DEFAULT_VALUE);
        return attributValue;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AttributValue createUpdatedEntity(EntityManager em) {
        AttributValue attributValue = new AttributValue()
            .value(UPDATED_VALUE);
        return attributValue;
    }

    @BeforeEach
    public void initTest() {
        attributValue = createEntity(em);
    }

    @Test
    @Transactional
    public void createAttributValue() throws Exception {
        int databaseSizeBeforeCreate = attributValueRepository.findAll().size();

        // Create the AttributValue
        restAttributValueMockMvc.perform(post("/api/attribut-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attributValue)))
            .andExpect(status().isCreated());

        // Validate the AttributValue in the database
        List<AttributValue> attributValueList = attributValueRepository.findAll();
        assertThat(attributValueList).hasSize(databaseSizeBeforeCreate + 1);
        AttributValue testAttributValue = attributValueList.get(attributValueList.size() - 1);
        assertThat(testAttributValue.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the AttributValue in Elasticsearch
        verify(mockAttributValueSearchRepository, times(1)).save(testAttributValue);
    }

    @Test
    @Transactional
    public void createAttributValueWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = attributValueRepository.findAll().size();

        // Create the AttributValue with an existing ID
        attributValue.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAttributValueMockMvc.perform(post("/api/attribut-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attributValue)))
            .andExpect(status().isBadRequest());

        // Validate the AttributValue in the database
        List<AttributValue> attributValueList = attributValueRepository.findAll();
        assertThat(attributValueList).hasSize(databaseSizeBeforeCreate);

        // Validate the AttributValue in Elasticsearch
        verify(mockAttributValueSearchRepository, times(0)).save(attributValue);
    }


    @Test
    @Transactional
    public void getAllAttributValues() throws Exception {
        // Initialize the database
        attributValueRepository.saveAndFlush(attributValue);

        // Get all the attributValueList
        restAttributValueMockMvc.perform(get("/api/attribut-values?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attributValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }
    
    @Test
    @Transactional
    public void getAttributValue() throws Exception {
        // Initialize the database
        attributValueRepository.saveAndFlush(attributValue);

        // Get the attributValue
        restAttributValueMockMvc.perform(get("/api/attribut-values/{id}", attributValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(attributValue.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAttributValue() throws Exception {
        // Get the attributValue
        restAttributValueMockMvc.perform(get("/api/attribut-values/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAttributValue() throws Exception {
        // Initialize the database
        attributValueRepository.saveAndFlush(attributValue);

        int databaseSizeBeforeUpdate = attributValueRepository.findAll().size();

        // Update the attributValue
        AttributValue updatedAttributValue = attributValueRepository.findById(attributValue.getId()).get();
        // Disconnect from session so that the updates on updatedAttributValue are not directly saved in db
        em.detach(updatedAttributValue);
        updatedAttributValue
            .value(UPDATED_VALUE);

        restAttributValueMockMvc.perform(put("/api/attribut-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAttributValue)))
            .andExpect(status().isOk());

        // Validate the AttributValue in the database
        List<AttributValue> attributValueList = attributValueRepository.findAll();
        assertThat(attributValueList).hasSize(databaseSizeBeforeUpdate);
        AttributValue testAttributValue = attributValueList.get(attributValueList.size() - 1);
        assertThat(testAttributValue.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the AttributValue in Elasticsearch
        verify(mockAttributValueSearchRepository, times(1)).save(testAttributValue);
    }

    @Test
    @Transactional
    public void updateNonExistingAttributValue() throws Exception {
        int databaseSizeBeforeUpdate = attributValueRepository.findAll().size();

        // Create the AttributValue

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttributValueMockMvc.perform(put("/api/attribut-values")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attributValue)))
            .andExpect(status().isBadRequest());

        // Validate the AttributValue in the database
        List<AttributValue> attributValueList = attributValueRepository.findAll();
        assertThat(attributValueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AttributValue in Elasticsearch
        verify(mockAttributValueSearchRepository, times(0)).save(attributValue);
    }

    @Test
    @Transactional
    public void deleteAttributValue() throws Exception {
        // Initialize the database
        attributValueRepository.saveAndFlush(attributValue);

        int databaseSizeBeforeDelete = attributValueRepository.findAll().size();

        // Delete the attributValue
        restAttributValueMockMvc.perform(delete("/api/attribut-values/{id}", attributValue.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AttributValue> attributValueList = attributValueRepository.findAll();
        assertThat(attributValueList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AttributValue in Elasticsearch
        verify(mockAttributValueSearchRepository, times(1)).deleteById(attributValue.getId());
    }

    @Test
    @Transactional
    public void searchAttributValue() throws Exception {
        // Initialize the database
        attributValueRepository.saveAndFlush(attributValue);
        when(mockAttributValueSearchRepository.search(queryStringQuery("id:" + attributValue.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(attributValue), PageRequest.of(0, 1), 1));
        // Search the attributValue
        restAttributValueMockMvc.perform(get("/api/_search/attribut-values?query=id:" + attributValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attributValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AttributValue.class);
        AttributValue attributValue1 = new AttributValue();
        attributValue1.setId(1L);
        AttributValue attributValue2 = new AttributValue();
        attributValue2.setId(attributValue1.getId());
        assertThat(attributValue1).isEqualTo(attributValue2);
        attributValue2.setId(2L);
        assertThat(attributValue1).isNotEqualTo(attributValue2);
        attributValue1.setId(null);
        assertThat(attributValue1).isNotEqualTo(attributValue2);
    }
}
