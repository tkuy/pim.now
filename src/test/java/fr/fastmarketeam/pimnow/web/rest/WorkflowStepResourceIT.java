package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.PimnowApp;
import fr.fastmarketeam.pimnow.domain.WorkflowStep;
import fr.fastmarketeam.pimnow.repository.WorkflowStepRepository;
import fr.fastmarketeam.pimnow.repository.search.WorkflowStepSearchRepository;
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
 * Integration tests for the {@link WorkflowStepResource} REST controller.
 */
@SpringBootTest(classes = PimnowApp.class)
public class WorkflowStepResourceIT {

    private static final String DEFAULT_ID_F = "AAAAAAAAAA";
    private static final String UPDATED_ID_F = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_RANK = 1;
    private static final Integer UPDATED_RANK = 2;
    private static final Integer SMALLER_RANK = 1 - 1;

    private static final Boolean DEFAULT_IS_INTEGRATION_STEP = false;
    private static final Boolean UPDATED_IS_INTEGRATION_STEP = true;

    @Autowired
    private WorkflowStepRepository workflowStepRepository;

    /**
     * This repository is mocked in the fr.fastmarketeam.pimnow.repository.search test package.
     *
     * @see fr.fastmarketeam.pimnow.repository.search.WorkflowStepSearchRepositoryMockConfiguration
     */
    @Autowired
    private WorkflowStepSearchRepository mockWorkflowStepSearchRepository;

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

    private MockMvc restWorkflowStepMockMvc;

    private WorkflowStep workflowStep;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final WorkflowStepResource workflowStepResource = new WorkflowStepResource(workflowStepRepository, mockWorkflowStepSearchRepository);
        this.restWorkflowStepMockMvc = MockMvcBuilders.standaloneSetup(workflowStepResource)
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
    public static WorkflowStep createEntity(EntityManager em) {
        WorkflowStep workflowStep = new WorkflowStep()
            .idF(DEFAULT_ID_F)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .rank(DEFAULT_RANK)
            .isIntegrationStep(DEFAULT_IS_INTEGRATION_STEP);
        return workflowStep;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkflowStep createUpdatedEntity(EntityManager em) {
        WorkflowStep workflowStep = new WorkflowStep()
            .idF(UPDATED_ID_F)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .rank(UPDATED_RANK)
            .isIntegrationStep(UPDATED_IS_INTEGRATION_STEP);
        return workflowStep;
    }

    @BeforeEach
    public void initTest() {
        workflowStep = createEntity(em);
    }

    @Test
    @Transactional
    public void createWorkflowStep() throws Exception {
        int databaseSizeBeforeCreate = workflowStepRepository.findAll().size();

        // Create the WorkflowStep
        restWorkflowStepMockMvc.perform(post("/api/workflow-steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflowStep)))
            .andExpect(status().isCreated());

        // Validate the WorkflowStep in the database
        List<WorkflowStep> workflowStepList = workflowStepRepository.findAll();
        assertThat(workflowStepList).hasSize(databaseSizeBeforeCreate + 1);
        WorkflowStep testWorkflowStep = workflowStepList.get(workflowStepList.size() - 1);
        assertThat(testWorkflowStep.getIdF()).isEqualTo(DEFAULT_ID_F);
        assertThat(testWorkflowStep.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWorkflowStep.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testWorkflowStep.getRank()).isEqualTo(DEFAULT_RANK);
        assertThat(testWorkflowStep.isIsIntegrationStep()).isEqualTo(DEFAULT_IS_INTEGRATION_STEP);

        // Validate the WorkflowStep in Elasticsearch
        verify(mockWorkflowStepSearchRepository, times(1)).save(testWorkflowStep);
    }

    @Test
    @Transactional
    public void createWorkflowStepWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = workflowStepRepository.findAll().size();

        // Create the WorkflowStep with an existing ID
        workflowStep.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkflowStepMockMvc.perform(post("/api/workflow-steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflowStep)))
            .andExpect(status().isBadRequest());

        // Validate the WorkflowStep in the database
        List<WorkflowStep> workflowStepList = workflowStepRepository.findAll();
        assertThat(workflowStepList).hasSize(databaseSizeBeforeCreate);

        // Validate the WorkflowStep in Elasticsearch
        verify(mockWorkflowStepSearchRepository, times(0)).save(workflowStep);
    }


    @Test
    @Transactional
    public void checkIdFIsRequired() throws Exception {
        int databaseSizeBeforeTest = workflowStepRepository.findAll().size();
        // set the field null
        workflowStep.setIdF(null);

        // Create the WorkflowStep, which fails.

        restWorkflowStepMockMvc.perform(post("/api/workflow-steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflowStep)))
            .andExpect(status().isBadRequest());

        List<WorkflowStep> workflowStepList = workflowStepRepository.findAll();
        assertThat(workflowStepList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = workflowStepRepository.findAll().size();
        // set the field null
        workflowStep.setName(null);

        // Create the WorkflowStep, which fails.

        restWorkflowStepMockMvc.perform(post("/api/workflow-steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflowStep)))
            .andExpect(status().isBadRequest());

        List<WorkflowStep> workflowStepList = workflowStepRepository.findAll();
        assertThat(workflowStepList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRankIsRequired() throws Exception {
        int databaseSizeBeforeTest = workflowStepRepository.findAll().size();
        // set the field null
        workflowStep.setRank(null);

        // Create the WorkflowStep, which fails.

        restWorkflowStepMockMvc.perform(post("/api/workflow-steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflowStep)))
            .andExpect(status().isBadRequest());

        List<WorkflowStep> workflowStepList = workflowStepRepository.findAll();
        assertThat(workflowStepList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIsIntegrationStepIsRequired() throws Exception {
        int databaseSizeBeforeTest = workflowStepRepository.findAll().size();
        // set the field null
        workflowStep.setIsIntegrationStep(null);

        // Create the WorkflowStep, which fails.

        restWorkflowStepMockMvc.perform(post("/api/workflow-steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflowStep)))
            .andExpect(status().isBadRequest());

        List<WorkflowStep> workflowStepList = workflowStepRepository.findAll();
        assertThat(workflowStepList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWorkflowSteps() throws Exception {
        // Initialize the database
        workflowStepRepository.saveAndFlush(workflowStep);

        // Get all the workflowStepList
        restWorkflowStepMockMvc.perform(get("/api/workflow-steps?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workflowStep.getId().intValue())))
            .andExpect(jsonPath("$.[*].idF").value(hasItem(DEFAULT_ID_F.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)))
            .andExpect(jsonPath("$.[*].isIntegrationStep").value(hasItem(DEFAULT_IS_INTEGRATION_STEP.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getWorkflowStep() throws Exception {
        // Initialize the database
        workflowStepRepository.saveAndFlush(workflowStep);

        // Get the workflowStep
        restWorkflowStepMockMvc.perform(get("/api/workflow-steps/{id}", workflowStep.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(workflowStep.getId().intValue()))
            .andExpect(jsonPath("$.idF").value(DEFAULT_ID_F.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.rank").value(DEFAULT_RANK))
            .andExpect(jsonPath("$.isIntegrationStep").value(DEFAULT_IS_INTEGRATION_STEP.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingWorkflowStep() throws Exception {
        // Get the workflowStep
        restWorkflowStepMockMvc.perform(get("/api/workflow-steps/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWorkflowStep() throws Exception {
        // Initialize the database
        workflowStepRepository.saveAndFlush(workflowStep);

        int databaseSizeBeforeUpdate = workflowStepRepository.findAll().size();

        // Update the workflowStep
        WorkflowStep updatedWorkflowStep = workflowStepRepository.findById(workflowStep.getId()).get();
        // Disconnect from session so that the updates on updatedWorkflowStep are not directly saved in db
        em.detach(updatedWorkflowStep);
        updatedWorkflowStep
            .idF(UPDATED_ID_F)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .rank(UPDATED_RANK)
            .isIntegrationStep(UPDATED_IS_INTEGRATION_STEP);

        restWorkflowStepMockMvc.perform(put("/api/workflow-steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWorkflowStep)))
            .andExpect(status().isOk());

        // Validate the WorkflowStep in the database
        List<WorkflowStep> workflowStepList = workflowStepRepository.findAll();
        assertThat(workflowStepList).hasSize(databaseSizeBeforeUpdate);
        WorkflowStep testWorkflowStep = workflowStepList.get(workflowStepList.size() - 1);
        assertThat(testWorkflowStep.getIdF()).isEqualTo(UPDATED_ID_F);
        assertThat(testWorkflowStep.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWorkflowStep.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testWorkflowStep.getRank()).isEqualTo(UPDATED_RANK);
        assertThat(testWorkflowStep.isIsIntegrationStep()).isEqualTo(UPDATED_IS_INTEGRATION_STEP);

        // Validate the WorkflowStep in Elasticsearch
        verify(mockWorkflowStepSearchRepository, times(1)).save(testWorkflowStep);
    }

    @Test
    @Transactional
    public void updateNonExistingWorkflowStep() throws Exception {
        int databaseSizeBeforeUpdate = workflowStepRepository.findAll().size();

        // Create the WorkflowStep

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkflowStepMockMvc.perform(put("/api/workflow-steps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflowStep)))
            .andExpect(status().isBadRequest());

        // Validate the WorkflowStep in the database
        List<WorkflowStep> workflowStepList = workflowStepRepository.findAll();
        assertThat(workflowStepList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WorkflowStep in Elasticsearch
        verify(mockWorkflowStepSearchRepository, times(0)).save(workflowStep);
    }

    @Test
    @Transactional
    public void deleteWorkflowStep() throws Exception {
        // Initialize the database
        workflowStepRepository.saveAndFlush(workflowStep);

        int databaseSizeBeforeDelete = workflowStepRepository.findAll().size();

        // Delete the workflowStep
        restWorkflowStepMockMvc.perform(delete("/api/workflow-steps/{id}", workflowStep.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WorkflowStep> workflowStepList = workflowStepRepository.findAll();
        assertThat(workflowStepList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the WorkflowStep in Elasticsearch
        verify(mockWorkflowStepSearchRepository, times(1)).deleteById(workflowStep.getId());
    }

    @Test
    @Transactional
    public void searchWorkflowStep() throws Exception {
        // Initialize the database
        workflowStepRepository.saveAndFlush(workflowStep);
        when(mockWorkflowStepSearchRepository.search(queryStringQuery("id:" + workflowStep.getId())))
            .thenReturn(Collections.singletonList(workflowStep));
        // Search the workflowStep
        restWorkflowStepMockMvc.perform(get("/api/_search/workflow-steps?query=id:" + workflowStep.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workflowStep.getId().intValue())))
            .andExpect(jsonPath("$.[*].idF").value(hasItem(DEFAULT_ID_F)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)))
            .andExpect(jsonPath("$.[*].isIntegrationStep").value(hasItem(DEFAULT_IS_INTEGRATION_STEP.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkflowStep.class);
        WorkflowStep workflowStep1 = new WorkflowStep();
        workflowStep1.setId(1L);
        WorkflowStep workflowStep2 = new WorkflowStep();
        workflowStep2.setId(workflowStep1.getId());
        assertThat(workflowStep1).isEqualTo(workflowStep2);
        workflowStep2.setId(2L);
        assertThat(workflowStep1).isNotEqualTo(workflowStep2);
        workflowStep1.setId(null);
        assertThat(workflowStep1).isNotEqualTo(workflowStep2);
    }
}
