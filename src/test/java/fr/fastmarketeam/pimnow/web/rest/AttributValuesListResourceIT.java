package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.PimnowApp;
import fr.fastmarketeam.pimnow.domain.AttributValuesList;
import fr.fastmarketeam.pimnow.repository.AttributValuesListRepository;
import fr.fastmarketeam.pimnow.repository.search.AttributValuesListSearchRepository;
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
 * Integration tests for the {@link AttributValuesListResource} REST controller.
 */
@SpringBootTest(classes = PimnowApp.class)
public class AttributValuesListResourceIT {

    @Autowired
    private AttributValuesListRepository attributValuesListRepository;

    /**
     * This repository is mocked in the fr.fastmarketeam.pimnow.repository.search test package.
     *
     * @see fr.fastmarketeam.pimnow.repository.search.AttributValuesListSearchRepositoryMockConfiguration
     */
    @Autowired
    private AttributValuesListSearchRepository mockAttributValuesListSearchRepository;

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

    private MockMvc restAttributValuesListMockMvc;

    private AttributValuesList attributValuesList;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AttributValuesListResource attributValuesListResource = new AttributValuesListResource(attributValuesListRepository, mockAttributValuesListSearchRepository);
        this.restAttributValuesListMockMvc = MockMvcBuilders.standaloneSetup(attributValuesListResource)
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
    public static AttributValuesList createEntity(EntityManager em) {
        AttributValuesList attributValuesList = new AttributValuesList();
        return attributValuesList;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AttributValuesList createUpdatedEntity(EntityManager em) {
        AttributValuesList attributValuesList = new AttributValuesList();
        return attributValuesList;
    }

    @BeforeEach
    public void initTest() {
        attributValuesList = createEntity(em);
    }

    @Test
    @Transactional
    public void createAttributValuesList() throws Exception {
        int databaseSizeBeforeCreate = attributValuesListRepository.findAll().size();

        // Create the AttributValuesList
        restAttributValuesListMockMvc.perform(post("/api/attribut-values-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attributValuesList)))
            .andExpect(status().isCreated());

        // Validate the AttributValuesList in the database
        List<AttributValuesList> attributValuesListList = attributValuesListRepository.findAll();
        assertThat(attributValuesListList).hasSize(databaseSizeBeforeCreate + 1);
        AttributValuesList testAttributValuesList = attributValuesListList.get(attributValuesListList.size() - 1);

        // Validate the AttributValuesList in Elasticsearch
        verify(mockAttributValuesListSearchRepository, times(1)).save(testAttributValuesList);
    }

    @Test
    @Transactional
    public void createAttributValuesListWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = attributValuesListRepository.findAll().size();

        // Create the AttributValuesList with an existing ID
        attributValuesList.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAttributValuesListMockMvc.perform(post("/api/attribut-values-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attributValuesList)))
            .andExpect(status().isBadRequest());

        // Validate the AttributValuesList in the database
        List<AttributValuesList> attributValuesListList = attributValuesListRepository.findAll();
        assertThat(attributValuesListList).hasSize(databaseSizeBeforeCreate);

        // Validate the AttributValuesList in Elasticsearch
        verify(mockAttributValuesListSearchRepository, times(0)).save(attributValuesList);
    }


    @Test
    @Transactional
    public void getAllAttributValuesLists() throws Exception {
        // Initialize the database
        attributValuesListRepository.saveAndFlush(attributValuesList);

        // Get all the attributValuesListList
        restAttributValuesListMockMvc.perform(get("/api/attribut-values-lists?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attributValuesList.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getAttributValuesList() throws Exception {
        // Initialize the database
        attributValuesListRepository.saveAndFlush(attributValuesList);

        // Get the attributValuesList
        restAttributValuesListMockMvc.perform(get("/api/attribut-values-lists/{id}", attributValuesList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(attributValuesList.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingAttributValuesList() throws Exception {
        // Get the attributValuesList
        restAttributValuesListMockMvc.perform(get("/api/attribut-values-lists/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAttributValuesList() throws Exception {
        // Initialize the database
        attributValuesListRepository.saveAndFlush(attributValuesList);

        int databaseSizeBeforeUpdate = attributValuesListRepository.findAll().size();

        // Update the attributValuesList
        AttributValuesList updatedAttributValuesList = attributValuesListRepository.findById(attributValuesList.getId()).get();
        // Disconnect from session so that the updates on updatedAttributValuesList are not directly saved in db
        em.detach(updatedAttributValuesList);

        restAttributValuesListMockMvc.perform(put("/api/attribut-values-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAttributValuesList)))
            .andExpect(status().isOk());

        // Validate the AttributValuesList in the database
        List<AttributValuesList> attributValuesListList = attributValuesListRepository.findAll();
        assertThat(attributValuesListList).hasSize(databaseSizeBeforeUpdate);
        AttributValuesList testAttributValuesList = attributValuesListList.get(attributValuesListList.size() - 1);

        // Validate the AttributValuesList in Elasticsearch
        verify(mockAttributValuesListSearchRepository, times(1)).save(testAttributValuesList);
    }

    @Test
    @Transactional
    public void updateNonExistingAttributValuesList() throws Exception {
        int databaseSizeBeforeUpdate = attributValuesListRepository.findAll().size();

        // Create the AttributValuesList

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttributValuesListMockMvc.perform(put("/api/attribut-values-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attributValuesList)))
            .andExpect(status().isBadRequest());

        // Validate the AttributValuesList in the database
        List<AttributValuesList> attributValuesListList = attributValuesListRepository.findAll();
        assertThat(attributValuesListList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AttributValuesList in Elasticsearch
        verify(mockAttributValuesListSearchRepository, times(0)).save(attributValuesList);
    }

    @Test
    @Transactional
    public void deleteAttributValuesList() throws Exception {
        // Initialize the database
        attributValuesListRepository.saveAndFlush(attributValuesList);

        int databaseSizeBeforeDelete = attributValuesListRepository.findAll().size();

        // Delete the attributValuesList
        restAttributValuesListMockMvc.perform(delete("/api/attribut-values-lists/{id}", attributValuesList.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AttributValuesList> attributValuesListList = attributValuesListRepository.findAll();
        assertThat(attributValuesListList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AttributValuesList in Elasticsearch
        verify(mockAttributValuesListSearchRepository, times(1)).deleteById(attributValuesList.getId());
    }

    @Test
    @Transactional
    public void searchAttributValuesList() throws Exception {
        // Initialize the database
        attributValuesListRepository.saveAndFlush(attributValuesList);
        when(mockAttributValuesListSearchRepository.search(queryStringQuery("id:" + attributValuesList.getId())))
            .thenReturn(Collections.singletonList(attributValuesList));
        // Search the attributValuesList
        restAttributValuesListMockMvc.perform(get("/api/_search/attribut-values-lists?query=id:" + attributValuesList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attributValuesList.getId().intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AttributValuesList.class);
        AttributValuesList attributValuesList1 = new AttributValuesList();
        attributValuesList1.setId(1L);
        AttributValuesList attributValuesList2 = new AttributValuesList();
        attributValuesList2.setId(attributValuesList1.getId());
        assertThat(attributValuesList1).isEqualTo(attributValuesList2);
        attributValuesList2.setId(2L);
        assertThat(attributValuesList1).isNotEqualTo(attributValuesList2);
        attributValuesList1.setId(null);
        assertThat(attributValuesList1).isNotEqualTo(attributValuesList2);
    }
}
