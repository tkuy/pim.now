package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.PimnowApp;
import fr.fastmarketeam.pimnow.domain.ValuesList;
import fr.fastmarketeam.pimnow.repository.ValuesListRepository;
import fr.fastmarketeam.pimnow.repository.search.ValuesListSearchRepository;
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
 * Integration tests for the {@link ValuesListResource} REST controller.
 */
@SpringBootTest(classes = PimnowApp.class)
public class ValuesListResourceIT {

    private static final String DEFAULT_ID_F = "AAAAAAAAAA";
    private static final String UPDATED_ID_F = "BBBBBBBBBB";

    @Autowired
    private ValuesListRepository valuesListRepository;

    /**
     * This repository is mocked in the fr.fastmarketeam.pimnow.repository.search test package.
     *
     * @see fr.fastmarketeam.pimnow.repository.search.ValuesListSearchRepositoryMockConfiguration
     */
    @Autowired
    private ValuesListSearchRepository mockValuesListSearchRepository;

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

    private MockMvc restValuesListMockMvc;

    private ValuesList valuesList;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ValuesListResource valuesListResource = new ValuesListResource(valuesListRepository, mockValuesListSearchRepository);
        this.restValuesListMockMvc = MockMvcBuilders.standaloneSetup(valuesListResource)
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
    public static ValuesList createEntity(EntityManager em) {
        ValuesList valuesList = new ValuesList()
            .idF(DEFAULT_ID_F);
        return valuesList;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ValuesList createUpdatedEntity(EntityManager em) {
        ValuesList valuesList = new ValuesList()
            .idF(UPDATED_ID_F);
        return valuesList;
    }

    @BeforeEach
    public void initTest() {
        valuesList = createEntity(em);
    }

    @Test
    @Transactional
    public void createValuesList() throws Exception {
        int databaseSizeBeforeCreate = valuesListRepository.findAll().size();

        // Create the ValuesList
        restValuesListMockMvc.perform(post("/api/values-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(valuesList)))
            .andExpect(status().isCreated());

        // Validate the ValuesList in the database
        List<ValuesList> valuesListList = valuesListRepository.findAll();
        assertThat(valuesListList).hasSize(databaseSizeBeforeCreate + 1);
        ValuesList testValuesList = valuesListList.get(valuesListList.size() - 1);
        assertThat(testValuesList.getIdF()).isEqualTo(DEFAULT_ID_F);

        // Validate the ValuesList in Elasticsearch
        verify(mockValuesListSearchRepository, times(1)).save(testValuesList);
    }

    @Test
    @Transactional
    public void createValuesListWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = valuesListRepository.findAll().size();

        // Create the ValuesList with an existing ID
        valuesList.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restValuesListMockMvc.perform(post("/api/values-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(valuesList)))
            .andExpect(status().isBadRequest());

        // Validate the ValuesList in the database
        List<ValuesList> valuesListList = valuesListRepository.findAll();
        assertThat(valuesListList).hasSize(databaseSizeBeforeCreate);

        // Validate the ValuesList in Elasticsearch
        verify(mockValuesListSearchRepository, times(0)).save(valuesList);
    }


    @Test
    @Transactional
    public void checkIdFIsRequired() throws Exception {
        int databaseSizeBeforeTest = valuesListRepository.findAll().size();
        // set the field null
        valuesList.setIdF(null);

        // Create the ValuesList, which fails.

        restValuesListMockMvc.perform(post("/api/values-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(valuesList)))
            .andExpect(status().isBadRequest());

        List<ValuesList> valuesListList = valuesListRepository.findAll();
        assertThat(valuesListList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllValuesLists() throws Exception {
        // Initialize the database
        valuesListRepository.saveAndFlush(valuesList);

        // Get all the valuesListList
        restValuesListMockMvc.perform(get("/api/values-lists?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(valuesList.getId().intValue())))
            .andExpect(jsonPath("$.[*].idF").value(hasItem(DEFAULT_ID_F.toString())));
    }
    
    @Test
    @Transactional
    public void getValuesList() throws Exception {
        // Initialize the database
        valuesListRepository.saveAndFlush(valuesList);

        // Get the valuesList
        restValuesListMockMvc.perform(get("/api/values-lists/{id}", valuesList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(valuesList.getId().intValue()))
            .andExpect(jsonPath("$.idF").value(DEFAULT_ID_F.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingValuesList() throws Exception {
        // Get the valuesList
        restValuesListMockMvc.perform(get("/api/values-lists/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateValuesList() throws Exception {
        // Initialize the database
        valuesListRepository.saveAndFlush(valuesList);

        int databaseSizeBeforeUpdate = valuesListRepository.findAll().size();

        // Update the valuesList
        ValuesList updatedValuesList = valuesListRepository.findById(valuesList.getId()).get();
        // Disconnect from session so that the updates on updatedValuesList are not directly saved in db
        em.detach(updatedValuesList);
        updatedValuesList
            .idF(UPDATED_ID_F);

        restValuesListMockMvc.perform(put("/api/values-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedValuesList)))
            .andExpect(status().isOk());

        // Validate the ValuesList in the database
        List<ValuesList> valuesListList = valuesListRepository.findAll();
        assertThat(valuesListList).hasSize(databaseSizeBeforeUpdate);
        ValuesList testValuesList = valuesListList.get(valuesListList.size() - 1);
        assertThat(testValuesList.getIdF()).isEqualTo(UPDATED_ID_F);

        // Validate the ValuesList in Elasticsearch
        verify(mockValuesListSearchRepository, times(1)).save(testValuesList);
    }

    @Test
    @Transactional
    public void updateNonExistingValuesList() throws Exception {
        int databaseSizeBeforeUpdate = valuesListRepository.findAll().size();

        // Create the ValuesList

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restValuesListMockMvc.perform(put("/api/values-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(valuesList)))
            .andExpect(status().isBadRequest());

        // Validate the ValuesList in the database
        List<ValuesList> valuesListList = valuesListRepository.findAll();
        assertThat(valuesListList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ValuesList in Elasticsearch
        verify(mockValuesListSearchRepository, times(0)).save(valuesList);
    }

    @Test
    @Transactional
    public void deleteValuesList() throws Exception {
        // Initialize the database
        valuesListRepository.saveAndFlush(valuesList);

        int databaseSizeBeforeDelete = valuesListRepository.findAll().size();

        // Delete the valuesList
        restValuesListMockMvc.perform(delete("/api/values-lists/{id}", valuesList.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ValuesList> valuesListList = valuesListRepository.findAll();
        assertThat(valuesListList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ValuesList in Elasticsearch
        verify(mockValuesListSearchRepository, times(1)).deleteById(valuesList.getId());
    }

    @Test
    @Transactional
    public void searchValuesList() throws Exception {
        // Initialize the database
        valuesListRepository.saveAndFlush(valuesList);
        when(mockValuesListSearchRepository.search(queryStringQuery("id:" + valuesList.getId())))
            .thenReturn(Collections.singletonList(valuesList));
        // Search the valuesList
        restValuesListMockMvc.perform(get("/api/_search/values-lists?query=id:" + valuesList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(valuesList.getId().intValue())))
            .andExpect(jsonPath("$.[*].idF").value(hasItem(DEFAULT_ID_F)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ValuesList.class);
        ValuesList valuesList1 = new ValuesList();
        valuesList1.setId(1L);
        ValuesList valuesList2 = new ValuesList();
        valuesList2.setId(valuesList1.getId());
        assertThat(valuesList1).isEqualTo(valuesList2);
        valuesList2.setId(2L);
        assertThat(valuesList1).isNotEqualTo(valuesList2);
        valuesList1.setId(null);
        assertThat(valuesList1).isNotEqualTo(valuesList2);
    }
}
