package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.PimnowApp;
import fr.fastmarketeam.pimnow.domain.ValuesListItem;
import fr.fastmarketeam.pimnow.repository.ValuesListItemRepository;
import fr.fastmarketeam.pimnow.repository.search.ValuesListItemSearchRepository;
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
 * Integration tests for the {@link ValuesListItemResource} REST controller.
 */
@SpringBootTest(classes = PimnowApp.class)
public class ValuesListItemResourceIT {

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    @Autowired
    private ValuesListItemRepository valuesListItemRepository;

    /**
     * This repository is mocked in the fr.fastmarketeam.pimnow.repository.search test package.
     *
     * @see fr.fastmarketeam.pimnow.repository.search.ValuesListItemSearchRepositoryMockConfiguration
     */
    @Autowired
    private ValuesListItemSearchRepository mockValuesListItemSearchRepository;

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

    private MockMvc restValuesListItemMockMvc;

    private ValuesListItem valuesListItem;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ValuesListItemResource valuesListItemResource = new ValuesListItemResource(valuesListItemRepository, mockValuesListItemSearchRepository);
        this.restValuesListItemMockMvc = MockMvcBuilders.standaloneSetup(valuesListItemResource)
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
    public static ValuesListItem createEntity(EntityManager em) {
        ValuesListItem valuesListItem = new ValuesListItem()
            .value(DEFAULT_VALUE);
        return valuesListItem;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ValuesListItem createUpdatedEntity(EntityManager em) {
        ValuesListItem valuesListItem = new ValuesListItem()
            .value(UPDATED_VALUE);
        return valuesListItem;
    }

    @BeforeEach
    public void initTest() {
        valuesListItem = createEntity(em);
    }

    @Test
    @Transactional
    public void createValuesListItem() throws Exception {
        int databaseSizeBeforeCreate = valuesListItemRepository.findAll().size();

        // Create the ValuesListItem
        restValuesListItemMockMvc.perform(post("/api/values-list-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(valuesListItem)))
            .andExpect(status().isCreated());

        // Validate the ValuesListItem in the database
        List<ValuesListItem> valuesListItemList = valuesListItemRepository.findAll();
        assertThat(valuesListItemList).hasSize(databaseSizeBeforeCreate + 1);
        ValuesListItem testValuesListItem = valuesListItemList.get(valuesListItemList.size() - 1);
        assertThat(testValuesListItem.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the ValuesListItem in Elasticsearch
        verify(mockValuesListItemSearchRepository, times(1)).save(testValuesListItem);
    }

    @Test
    @Transactional
    public void createValuesListItemWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = valuesListItemRepository.findAll().size();

        // Create the ValuesListItem with an existing ID
        valuesListItem.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restValuesListItemMockMvc.perform(post("/api/values-list-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(valuesListItem)))
            .andExpect(status().isBadRequest());

        // Validate the ValuesListItem in the database
        List<ValuesListItem> valuesListItemList = valuesListItemRepository.findAll();
        assertThat(valuesListItemList).hasSize(databaseSizeBeforeCreate);

        // Validate the ValuesListItem in Elasticsearch
        verify(mockValuesListItemSearchRepository, times(0)).save(valuesListItem);
    }


    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = valuesListItemRepository.findAll().size();
        // set the field null
        valuesListItem.setValue(null);

        // Create the ValuesListItem, which fails.

        restValuesListItemMockMvc.perform(post("/api/values-list-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(valuesListItem)))
            .andExpect(status().isBadRequest());

        List<ValuesListItem> valuesListItemList = valuesListItemRepository.findAll();
        assertThat(valuesListItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllValuesListItems() throws Exception {
        // Initialize the database
        valuesListItemRepository.saveAndFlush(valuesListItem);

        // Get all the valuesListItemList
        restValuesListItemMockMvc.perform(get("/api/values-list-items?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(valuesListItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }
    
    @Test
    @Transactional
    public void getValuesListItem() throws Exception {
        // Initialize the database
        valuesListItemRepository.saveAndFlush(valuesListItem);

        // Get the valuesListItem
        restValuesListItemMockMvc.perform(get("/api/values-list-items/{id}", valuesListItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(valuesListItem.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingValuesListItem() throws Exception {
        // Get the valuesListItem
        restValuesListItemMockMvc.perform(get("/api/values-list-items/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateValuesListItem() throws Exception {
        // Initialize the database
        valuesListItemRepository.saveAndFlush(valuesListItem);

        int databaseSizeBeforeUpdate = valuesListItemRepository.findAll().size();

        // Update the valuesListItem
        ValuesListItem updatedValuesListItem = valuesListItemRepository.findById(valuesListItem.getId()).get();
        // Disconnect from session so that the updates on updatedValuesListItem are not directly saved in db
        em.detach(updatedValuesListItem);
        updatedValuesListItem
            .value(UPDATED_VALUE);

        restValuesListItemMockMvc.perform(put("/api/values-list-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedValuesListItem)))
            .andExpect(status().isOk());

        // Validate the ValuesListItem in the database
        List<ValuesListItem> valuesListItemList = valuesListItemRepository.findAll();
        assertThat(valuesListItemList).hasSize(databaseSizeBeforeUpdate);
        ValuesListItem testValuesListItem = valuesListItemList.get(valuesListItemList.size() - 1);
        assertThat(testValuesListItem.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the ValuesListItem in Elasticsearch
        verify(mockValuesListItemSearchRepository, times(1)).save(testValuesListItem);
    }

    @Test
    @Transactional
    public void updateNonExistingValuesListItem() throws Exception {
        int databaseSizeBeforeUpdate = valuesListItemRepository.findAll().size();

        // Create the ValuesListItem

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restValuesListItemMockMvc.perform(put("/api/values-list-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(valuesListItem)))
            .andExpect(status().isBadRequest());

        // Validate the ValuesListItem in the database
        List<ValuesListItem> valuesListItemList = valuesListItemRepository.findAll();
        assertThat(valuesListItemList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ValuesListItem in Elasticsearch
        verify(mockValuesListItemSearchRepository, times(0)).save(valuesListItem);
    }

    @Test
    @Transactional
    public void deleteValuesListItem() throws Exception {
        // Initialize the database
        valuesListItemRepository.saveAndFlush(valuesListItem);

        int databaseSizeBeforeDelete = valuesListItemRepository.findAll().size();

        // Delete the valuesListItem
        restValuesListItemMockMvc.perform(delete("/api/values-list-items/{id}", valuesListItem.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ValuesListItem> valuesListItemList = valuesListItemRepository.findAll();
        assertThat(valuesListItemList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ValuesListItem in Elasticsearch
        verify(mockValuesListItemSearchRepository, times(1)).deleteById(valuesListItem.getId());
    }

    @Test
    @Transactional
    public void searchValuesListItem() throws Exception {
        // Initialize the database
        valuesListItemRepository.saveAndFlush(valuesListItem);
        when(mockValuesListItemSearchRepository.search(queryStringQuery("id:" + valuesListItem.getId())))
            .thenReturn(Collections.singletonList(valuesListItem));
        // Search the valuesListItem
        restValuesListItemMockMvc.perform(get("/api/_search/values-list-items?query=id:" + valuesListItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(valuesListItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ValuesListItem.class);
        ValuesListItem valuesListItem1 = new ValuesListItem();
        valuesListItem1.setId(1L);
        ValuesListItem valuesListItem2 = new ValuesListItem();
        valuesListItem2.setId(valuesListItem1.getId());
        assertThat(valuesListItem1).isEqualTo(valuesListItem2);
        valuesListItem2.setId(2L);
        assertThat(valuesListItem1).isNotEqualTo(valuesListItem2);
        valuesListItem1.setId(null);
        assertThat(valuesListItem1).isNotEqualTo(valuesListItem2);
    }
}
