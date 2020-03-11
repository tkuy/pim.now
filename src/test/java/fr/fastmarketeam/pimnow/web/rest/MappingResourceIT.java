package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.PimnowApp;
import fr.fastmarketeam.pimnow.domain.Mapping;
import fr.fastmarketeam.pimnow.repository.AssociationRepository;
import fr.fastmarketeam.pimnow.repository.AttributRepository;
import fr.fastmarketeam.pimnow.repository.MappingRepository;
import fr.fastmarketeam.pimnow.repository.UserExtraRepository;
import fr.fastmarketeam.pimnow.repository.search.MappingSearchRepository;
import fr.fastmarketeam.pimnow.service.util.UserCustomerUtil;
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
 * Integration tests for the {@link MappingResource} REST controller.
 */
@SpringBootTest(classes = PimnowApp.class)
public class MappingResourceIT {

    private static final String DEFAULT_ID_F = "AAAAAAAAAA";
    private static final String UPDATED_ID_F = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_SEPARATOR = "AAAAAAAAAA";
    private static final String UPDATED_SEPARATOR = "BBBBBBBBBB";

    @Autowired
    private MappingRepository mappingRepository;

    /**
     * This repository is mocked in the fr.fastmarketeam.pimnow.repository.search test package.
     *
     * @see fr.fastmarketeam.pimnow.repository.search.MappingSearchRepositoryMockConfiguration
     */
    @Autowired
    private MappingSearchRepository mockMappingSearchRepository;

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

    @Autowired
    private UserExtraRepository userExtraRepository;

    @Autowired
    private AttributRepository attributRepository;

    @Autowired
    private AssociationRepository associationRepository;

    private MockMvc restMappingMockMvc;

    private Mapping mapping;

    private UserCustomerUtil userCustomerUtil;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MappingResource mappingResource = new MappingResource(mappingRepository, mockMappingSearchRepository, userExtraRepository, attributRepository, associationRepository, userCustomerUtil);
        this.restMappingMockMvc = MockMvcBuilders.standaloneSetup(mappingResource)
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
    public static Mapping createEntity(EntityManager em) {
        Mapping mapping = new Mapping()
            .idF(DEFAULT_ID_F)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .separator(DEFAULT_SEPARATOR);
        return mapping;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mapping createUpdatedEntity(EntityManager em) {
        Mapping mapping = new Mapping()
            .idF(UPDATED_ID_F)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .separator(UPDATED_SEPARATOR);
        return mapping;
    }

    @BeforeEach
    public void initTest() {
        mapping = createEntity(em);
    }

    @Test
    @Transactional
    public void createMapping() throws Exception {
        int databaseSizeBeforeCreate = mappingRepository.findAll().size();

        // Create the Mapping
        restMappingMockMvc.perform(post("/api/mappings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mapping)))
            .andExpect(status().isCreated());

        // Validate the Mapping in the database
        List<Mapping> mappingList = mappingRepository.findAll();
        assertThat(mappingList).hasSize(databaseSizeBeforeCreate + 1);
        Mapping testMapping = mappingList.get(mappingList.size() - 1);
        assertThat(testMapping.getIdF()).isEqualTo(DEFAULT_ID_F);
        assertThat(testMapping.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMapping.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMapping.getSeparator()).isEqualTo(DEFAULT_SEPARATOR);

        // Validate the Mapping in Elasticsearch
        verify(mockMappingSearchRepository, times(1)).save(testMapping);
    }

    @Test
    @Transactional
    public void createMappingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = mappingRepository.findAll().size();

        // Create the Mapping with an existing ID
        mapping.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMappingMockMvc.perform(post("/api/mappings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mapping)))
            .andExpect(status().isBadRequest());

        // Validate the Mapping in the database
        List<Mapping> mappingList = mappingRepository.findAll();
        assertThat(mappingList).hasSize(databaseSizeBeforeCreate);

        // Validate the Mapping in Elasticsearch
        verify(mockMappingSearchRepository, times(0)).save(mapping);
    }


    @Test
    @Transactional
    public void checkIdFIsRequired() throws Exception {
        int databaseSizeBeforeTest = mappingRepository.findAll().size();
        // set the field null
        mapping.setIdF(null);

        // Create the Mapping, which fails.

        restMappingMockMvc.perform(post("/api/mappings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mapping)))
            .andExpect(status().isBadRequest());

        List<Mapping> mappingList = mappingRepository.findAll();
        assertThat(mappingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = mappingRepository.findAll().size();
        // set the field null
        mapping.setName(null);

        // Create the Mapping, which fails.

        restMappingMockMvc.perform(post("/api/mappings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mapping)))
            .andExpect(status().isBadRequest());

        List<Mapping> mappingList = mappingRepository.findAll();
        assertThat(mappingList).hasSize(databaseSizeBeforeTest);
    }

    @Transactional
    public void getAllMappings() throws Exception {
        // Initialize the database
        mappingRepository.saveAndFlush(mapping);

        // Get all the mappingList
        restMappingMockMvc.perform(get("/api/mappings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mapping.getId().intValue())))
            .andExpect(jsonPath("$.[*].idF").value(hasItem(DEFAULT_ID_F.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].separator").value(hasItem(DEFAULT_SEPARATOR.toString())));
    }

    @Test
    @Transactional
    public void getMapping() throws Exception {
        // Initialize the database
        mappingRepository.saveAndFlush(mapping);

        // Get the mapping
        restMappingMockMvc.perform(get("/api/mappings/{id}", mapping.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(mapping.getId().intValue()))
            .andExpect(jsonPath("$.idF").value(DEFAULT_ID_F.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.separator").value(DEFAULT_SEPARATOR.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMapping() throws Exception {
        // Get the mapping
        restMappingMockMvc.perform(get("/api/mappings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    /*@Test
    @Transactional
    public void updateMapping() throws Exception {
        // Initialize the database
        mappingRepository.saveAndFlush(mapping);

        int databaseSizeBeforeUpdate = mappingRepository.findAll().size();

        // Update the mapping
        Mapping updatedMapping = mappingRepository.findById(mapping.getId()).get();
        // Disconnect from session so that the updates on updatedMapping are not directly saved in db
        em.detach(updatedMapping);
        updatedMapping
            .idF(UPDATED_ID_F)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .separator(UPDATED_SEPARATOR);

        restMappingMockMvc.perform(put("/api/mappings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMapping)))
            .andExpect(status().isOk());

        // Validate the Mapping in the database
        List<Mapping> mappingList = mappingRepository.findAll();
        assertThat(mappingList).hasSize(databaseSizeBeforeUpdate);
        Mapping testMapping = mappingList.get(mappingList.size() - 1);
        assertThat(testMapping.getIdF()).isEqualTo(UPDATED_ID_F);
        assertThat(testMapping.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMapping.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMapping.getSeparator()).isEqualTo(UPDATED_SEPARATOR);

        // Validate the Mapping in Elasticsearch
        verify(mockMappingSearchRepository, times(1)).save(testMapping);
    }

    @Test
    @Transactional
    public void updateNonExistingMapping() throws Exception {
        int databaseSizeBeforeUpdate = mappingRepository.findAll().size();

        // Create the Mapping

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMappingMockMvc.perform(put("/api/mappings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mapping)))
            .andExpect(status().isBadRequest());

        // Validate the Mapping in the database
        List<Mapping> mappingList = mappingRepository.findAll();
        assertThat(mappingList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Mapping in Elasticsearch
        verify(mockMappingSearchRepository, times(0)).save(mapping);
    }

    @Test
    @Transactional
    public void deleteMapping() throws Exception {
        // Initialize the database
        mappingRepository.saveAndFlush(mapping);

        int databaseSizeBeforeDelete = mappingRepository.findAll().size();

        // Delete the mapping
        restMappingMockMvc.perform(delete("/api/mappings/{id}", mapping.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Mapping> mappingList = mappingRepository.findAll();
        assertThat(mappingList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Mapping in Elasticsearch
        verify(mockMappingSearchRepository, times(1)).deleteById(mapping.getId());
    }*/

    @Test
    @Transactional
    public void searchMapping() throws Exception {
        // Initialize the database
        mappingRepository.saveAndFlush(mapping);
        when(mockMappingSearchRepository.search(queryStringQuery("id:" + mapping.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(mapping), PageRequest.of(0, 1), 1));
        // Search the mapping
        restMappingMockMvc.perform(get("/api/_search/mappings?query=id:" + mapping.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mapping.getId().intValue())))
            .andExpect(jsonPath("$.[*].idF").value(hasItem(DEFAULT_ID_F)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].separator").value(hasItem(DEFAULT_SEPARATOR)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Mapping.class);
        Mapping mapping1 = new Mapping();
        mapping1.setId(1L);
        Mapping mapping2 = new Mapping();
        mapping2.setId(mapping1.getId());
        assertThat(mapping1).isEqualTo(mapping2);
        mapping2.setId(2L);
        assertThat(mapping1).isNotEqualTo(mapping2);
        mapping1.setId(null);
        assertThat(mapping1).isNotEqualTo(mapping2);
    }
}
