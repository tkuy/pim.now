package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.PimnowApp;
import fr.fastmarketeam.pimnow.domain.Attribut;
import fr.fastmarketeam.pimnow.domain.enumeration.AttributType;
import fr.fastmarketeam.pimnow.repository.AttributRepository;
import fr.fastmarketeam.pimnow.repository.search.AttributSearchRepository;
import fr.fastmarketeam.pimnow.service.util.UserCustomerUtil;
import fr.fastmarketeam.pimnow.web.rest.errors.ExceptionTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
 * Integration tests for the {@link AttributResource} REST controller.
 */
@SpringBootTest(classes = PimnowApp.class)
public class AttributResourceIT {

    private static final String DEFAULT_ID_F = "AAAAAAAAAA";
    private static final String UPDATED_ID_F = "BBBBBBBBBB";

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final AttributType DEFAULT_TYPE = AttributType.TEXT;
    private static final AttributType UPDATED_TYPE = AttributType.RESSOURCE;

    @Autowired
    private AttributRepository attributRepository;

    /**
     * This repository is mocked in the fr.fastmarketeam.pimnow.repository.search test package.
     *
     * @see fr.fastmarketeam.pimnow.repository.search.AttributSearchRepositoryMockConfiguration
     */
    @Autowired
    private AttributSearchRepository mockAttributSearchRepository;

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

    @Mock
    private UserCustomerUtil userCustomerUtil;

    private MockMvc restAttributMockMvc;

    private Attribut attribut;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AttributResource attributResource = new AttributResource(attributRepository, mockAttributSearchRepository, userCustomerUtil);
        this.restAttributMockMvc = MockMvcBuilders.standaloneSetup(attributResource)
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
    public static Attribut createEntity(EntityManager em) {
        Attribut attribut = new Attribut()
            .idF(DEFAULT_ID_F)
            .nom(DEFAULT_NOM)
            .type(DEFAULT_TYPE);
        return attribut;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Attribut createUpdatedEntity(EntityManager em) {
        Attribut attribut = new Attribut()
            .idF(UPDATED_ID_F)
            .nom(UPDATED_NOM)
            .type(UPDATED_TYPE);
        return attribut;
    }

    @BeforeEach
    public void initTest() {
        attribut = createEntity(em);
    }

    @Test
    @Transactional
    public void createAttribut() throws Exception {
        int databaseSizeBeforeCreate = attributRepository.findAll().size();

        // Create the Attribut
        restAttributMockMvc.perform(post("/api/attributs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attribut)))
            .andExpect(status().isCreated());

        // Validate the Attribut in the database
        List<Attribut> attributList = attributRepository.findAll();
        assertThat(attributList).hasSize(databaseSizeBeforeCreate + 1);
        Attribut testAttribut = attributList.get(attributList.size() - 1);
        assertThat(testAttribut.getIdF()).isEqualTo(DEFAULT_ID_F);
        assertThat(testAttribut.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testAttribut.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the Attribut in Elasticsearch
        verify(mockAttributSearchRepository, times(1)).save(testAttribut);
    }

    @Test
    @Transactional
    public void createAttributWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = attributRepository.findAll().size();

        // Create the Attribut with an existing ID
        attribut.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAttributMockMvc.perform(post("/api/attributs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attribut)))
            .andExpect(status().isBadRequest());

        // Validate the Attribut in the database
        List<Attribut> attributList = attributRepository.findAll();
        assertThat(attributList).hasSize(databaseSizeBeforeCreate);

        // Validate the Attribut in Elasticsearch
        verify(mockAttributSearchRepository, times(0)).save(attribut);
    }


    @Test
    @Transactional
    public void checkIdFIsRequired() throws Exception {
        int databaseSizeBeforeTest = attributRepository.findAll().size();
        // set the field null
        attribut.setIdF(null);

        // Create the Attribut, which fails.

        restAttributMockMvc.perform(post("/api/attributs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attribut)))
            .andExpect(status().isBadRequest());

        List<Attribut> attributList = attributRepository.findAll();
        assertThat(attributList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = attributRepository.findAll().size();
        // set the field null
        attribut.setNom(null);

        // Create the Attribut, which fails.

        restAttributMockMvc.perform(post("/api/attributs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attribut)))
            .andExpect(status().isBadRequest());

        List<Attribut> attributList = attributRepository.findAll();
        assertThat(attributList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = attributRepository.findAll().size();
        // set the field null
        attribut.setType(null);

        // Create the Attribut, which fails.

        restAttributMockMvc.perform(post("/api/attributs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attribut)))
            .andExpect(status().isBadRequest());

        List<Attribut> attributList = attributRepository.findAll();
        assertThat(attributList).hasSize(databaseSizeBeforeTest);
    }

//    @Test
//    @Transactional
    public void getAllAttributs() throws Exception {
        // Initialize the database
        attributRepository.saveAndFlush(attribut);

        // Get all the attributList
        restAttributMockMvc.perform(get("/api/attributs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attribut.getId().intValue())))
            .andExpect(jsonPath("$.[*].idF").value(hasItem(DEFAULT_ID_F.toString())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

//    @Test
//    @Transactional
    public void getAttribut() throws Exception {
        // Initialize the database
        attributRepository.saveAndFlush(attribut);

        // Get the attribut
        restAttributMockMvc.perform(get("/api/attributs/{id}", attribut.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(attribut.getId().intValue()))
            .andExpect(jsonPath("$.idF").value(DEFAULT_ID_F.toString()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

//    @Test
//    @Transactional
    public void getNonExistingAttribut() throws Exception {
        // Get the attribut
        restAttributMockMvc.perform(get("/api/attributs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAttribut() throws Exception {
        // Initialize the database
        attributRepository.saveAndFlush(attribut);

        int databaseSizeBeforeUpdate = attributRepository.findAll().size();

        // Update the attribut
        Attribut updatedAttribut = attributRepository.findById(attribut.getId()).get();
        // Disconnect from session so that the updates on updatedAttribut are not directly saved in db
        em.detach(updatedAttribut);
        updatedAttribut
            .idF(UPDATED_ID_F)
            .nom(UPDATED_NOM)
            .type(UPDATED_TYPE);

        restAttributMockMvc.perform(put("/api/attributs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAttribut)))
            .andExpect(status().isOk());

        // Validate the Attribut in the database
        List<Attribut> attributList = attributRepository.findAll();
        assertThat(attributList).hasSize(databaseSizeBeforeUpdate);
        Attribut testAttribut = attributList.get(attributList.size() - 1);
        assertThat(testAttribut.getIdF()).isEqualTo(UPDATED_ID_F);
        assertThat(testAttribut.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testAttribut.getType()).isEqualTo(UPDATED_TYPE);

        // Validate the Attribut in Elasticsearch
        verify(mockAttributSearchRepository, times(1)).save(testAttribut);
    }

    @Test
    @Transactional
    public void updateNonExistingAttribut() throws Exception {
        int databaseSizeBeforeUpdate = attributRepository.findAll().size();

        // Create the Attribut

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttributMockMvc.perform(put("/api/attributs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(attribut)))
            .andExpect(status().isBadRequest());

        // Validate the Attribut in the database
        List<Attribut> attributList = attributRepository.findAll();
        assertThat(attributList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Attribut in Elasticsearch
        verify(mockAttributSearchRepository, times(0)).save(attribut);
    }

    @Test
    @Transactional
    public void deleteAttribut() throws Exception {
        // Initialize the database
        attributRepository.saveAndFlush(attribut);

        int databaseSizeBeforeDelete = attributRepository.findAll().size();

        // Delete the attribut
        restAttributMockMvc.perform(delete("/api/attributs/{id}", attribut.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Attribut> attributList = attributRepository.findAll();
        assertThat(attributList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Attribut in Elasticsearch
        verify(mockAttributSearchRepository, times(1)).deleteById(attribut.getId());
    }

    @Test
    @Transactional
    public void searchAttribut() throws Exception {
        // Initialize the database
        attributRepository.saveAndFlush(attribut);
        when(mockAttributSearchRepository.search(queryStringQuery("id:" + attribut.getId())))
            .thenReturn(Collections.singletonList(attribut));
        // Search the attribut
        restAttributMockMvc.perform(get("/api/_search/attributs?query=id:" + attribut.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attribut.getId().intValue())))
            .andExpect(jsonPath("$.[*].idF").value(hasItem(DEFAULT_ID_F)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Attribut.class);
        Attribut attribut1 = new Attribut();
        attribut1.setId(1L);
        Attribut attribut2 = new Attribut();
        attribut2.setId(attribut1.getId());
        assertThat(attribut1).isEqualTo(attribut2);
        attribut2.setId(2L);
        assertThat(attribut1).isNotEqualTo(attribut2);
        attribut1.setId(null);
        assertThat(attribut1).isNotEqualTo(attribut2);
    }
}
