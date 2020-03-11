package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.PimnowApp;
import fr.fastmarketeam.pimnow.domain.Association;
import fr.fastmarketeam.pimnow.repository.AssociationRepository;
import fr.fastmarketeam.pimnow.repository.search.AssociationSearchRepository;
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
 * Integration tests for the {@link AssociationResource} REST controller.
 */
@SpringBootTest(classes = PimnowApp.class)
public class AssociationResourceIT {

    private static final String DEFAULT_COLUMN = "AAAAAAAAAA";
    private static final String UPDATED_COLUMN = "BBBBBBBBBB";

    private static final String DEFAULT_ID_F_ATTRIBUT = "AAAAAAAAAA";
    private static final String UPDATED_ID_F_ATTRIBUT = "BBBBBBBBBB";

    @Autowired
    private AssociationRepository associationRepository;

    /**
     * This repository is mocked in the fr.fastmarketeam.pimnow.repository.search test package.
     *
     * @see fr.fastmarketeam.pimnow.repository.search.AssociationSearchRepositoryMockConfiguration
     */
    @Autowired
    private AssociationSearchRepository mockAssociationSearchRepository;

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

    private MockMvc restAssociationMockMvc;

    private Association association;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AssociationResource associationResource = new AssociationResource(associationRepository, mockAssociationSearchRepository);
        this.restAssociationMockMvc = MockMvcBuilders.standaloneSetup(associationResource)
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
    public static Association createEntity(EntityManager em) {
        Association association = new Association()
            .column(DEFAULT_COLUMN)
            .idFAttribut(DEFAULT_ID_F_ATTRIBUT);
        return association;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Association createUpdatedEntity(EntityManager em) {
        Association association = new Association()
            .column(UPDATED_COLUMN)
            .idFAttribut(UPDATED_ID_F_ATTRIBUT);
        return association;
    }

    @BeforeEach
    public void initTest() {
        association = createEntity(em);
    }

    @Test
    @Transactional
    public void createAssociation() throws Exception {
        int databaseSizeBeforeCreate = associationRepository.findAll().size();

        // Create the Association
        restAssociationMockMvc.perform(post("/api/associations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(association)))
            .andExpect(status().isCreated());

        // Validate the Association in the database
        List<Association> associationList = associationRepository.findAll();
        assertThat(associationList).hasSize(databaseSizeBeforeCreate + 1);
        Association testAssociation = associationList.get(associationList.size() - 1);
        assertThat(testAssociation.getColumn()).isEqualTo(DEFAULT_COLUMN);
        assertThat(testAssociation.getIdFAttribut()).isEqualTo(DEFAULT_ID_F_ATTRIBUT);

        // Validate the Association in Elasticsearch
        verify(mockAssociationSearchRepository, times(1)).save(testAssociation);
    }

    @Test
    @Transactional
    public void createAssociationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = associationRepository.findAll().size();

        // Create the Association with an existing ID
        association.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssociationMockMvc.perform(post("/api/associations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(association)))
            .andExpect(status().isBadRequest());

        // Validate the Association in the database
        List<Association> associationList = associationRepository.findAll();
        assertThat(associationList).hasSize(databaseSizeBeforeCreate);

        // Validate the Association in Elasticsearch
        verify(mockAssociationSearchRepository, times(0)).save(association);
    }


    @Test
    @Transactional
    public void checkColumnIsRequired() throws Exception {
        int databaseSizeBeforeTest = associationRepository.findAll().size();
        // set the field null
        association.setColumn(null);

        // Create the Association, which fails.

        restAssociationMockMvc.perform(post("/api/associations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(association)))
            .andExpect(status().isBadRequest());

        List<Association> associationList = associationRepository.findAll();
        assertThat(associationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIdFAttributIsRequired() throws Exception {
        int databaseSizeBeforeTest = associationRepository.findAll().size();
        // set the field null
        association.setIdFAttribut(null);

        // Create the Association, which fails.

        restAssociationMockMvc.perform(post("/api/associations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(association)))
            .andExpect(status().isBadRequest());

        List<Association> associationList = associationRepository.findAll();
        assertThat(associationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAssociations() throws Exception {
        // Initialize the database
        associationRepository.saveAndFlush(association);

        // Get all the associationList
        restAssociationMockMvc.perform(get("/api/associations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(association.getId().intValue())))
            .andExpect(jsonPath("$.[*].column").value(hasItem(DEFAULT_COLUMN.toString())))
            .andExpect(jsonPath("$.[*].idFAttribut").value(hasItem(DEFAULT_ID_F_ATTRIBUT.toString())));
    }
    
    @Test
    @Transactional
    public void getAssociation() throws Exception {
        // Initialize the database
        associationRepository.saveAndFlush(association);

        // Get the association
        restAssociationMockMvc.perform(get("/api/associations/{id}", association.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(association.getId().intValue()))
            .andExpect(jsonPath("$.column").value(DEFAULT_COLUMN.toString()))
            .andExpect(jsonPath("$.idFAttribut").value(DEFAULT_ID_F_ATTRIBUT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAssociation() throws Exception {
        // Get the association
        restAssociationMockMvc.perform(get("/api/associations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAssociation() throws Exception {
        // Initialize the database
        associationRepository.saveAndFlush(association);

        int databaseSizeBeforeUpdate = associationRepository.findAll().size();

        // Update the association
        Association updatedAssociation = associationRepository.findById(association.getId()).get();
        // Disconnect from session so that the updates on updatedAssociation are not directly saved in db
        em.detach(updatedAssociation);
        updatedAssociation
            .column(UPDATED_COLUMN)
            .idFAttribut(UPDATED_ID_F_ATTRIBUT);

        restAssociationMockMvc.perform(put("/api/associations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAssociation)))
            .andExpect(status().isOk());

        // Validate the Association in the database
        List<Association> associationList = associationRepository.findAll();
        assertThat(associationList).hasSize(databaseSizeBeforeUpdate);
        Association testAssociation = associationList.get(associationList.size() - 1);
        assertThat(testAssociation.getColumn()).isEqualTo(UPDATED_COLUMN);
        assertThat(testAssociation.getIdFAttribut()).isEqualTo(UPDATED_ID_F_ATTRIBUT);

        // Validate the Association in Elasticsearch
        verify(mockAssociationSearchRepository, times(1)).save(testAssociation);
    }

    @Test
    @Transactional
    public void updateNonExistingAssociation() throws Exception {
        int databaseSizeBeforeUpdate = associationRepository.findAll().size();

        // Create the Association

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssociationMockMvc.perform(put("/api/associations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(association)))
            .andExpect(status().isBadRequest());

        // Validate the Association in the database
        List<Association> associationList = associationRepository.findAll();
        assertThat(associationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Association in Elasticsearch
        verify(mockAssociationSearchRepository, times(0)).save(association);
    }

    @Test
    @Transactional
    public void deleteAssociation() throws Exception {
        // Initialize the database
        associationRepository.saveAndFlush(association);

        int databaseSizeBeforeDelete = associationRepository.findAll().size();

        // Delete the association
        restAssociationMockMvc.perform(delete("/api/associations/{id}", association.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Association> associationList = associationRepository.findAll();
        assertThat(associationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Association in Elasticsearch
        verify(mockAssociationSearchRepository, times(1)).deleteById(association.getId());
    }

    @Test
    @Transactional
    public void searchAssociation() throws Exception {
        // Initialize the database
        associationRepository.saveAndFlush(association);
        when(mockAssociationSearchRepository.search(queryStringQuery("id:" + association.getId())))
            .thenReturn(Collections.singletonList(association));
        // Search the association
        restAssociationMockMvc.perform(get("/api/_search/associations?query=id:" + association.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(association.getId().intValue())))
            .andExpect(jsonPath("$.[*].column").value(hasItem(DEFAULT_COLUMN)))
            .andExpect(jsonPath("$.[*].idFAttribut").value(hasItem(DEFAULT_ID_F_ATTRIBUT)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Association.class);
        Association association1 = new Association();
        association1.setId(1L);
        Association association2 = new Association();
        association2.setId(association1.getId());
        assertThat(association1).isEqualTo(association2);
        association2.setId(2L);
        assertThat(association1).isNotEqualTo(association2);
        association1.setId(null);
        assertThat(association1).isNotEqualTo(association2);
    }
}
