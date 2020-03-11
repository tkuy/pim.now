package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.PimnowApp;
import fr.fastmarketeam.pimnow.domain.Workflow;
import fr.fastmarketeam.pimnow.repository.WorkflowRepository;
import fr.fastmarketeam.pimnow.repository.search.WorkflowSearchRepository;
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
 * Integration tests for the {@link WorkflowResource} REST controller.
 */
@SpringBootTest(classes = PimnowApp.class)
public class WorkflowResourceIT {

    private static final String DEFAULT_ID_F = "AAAAAAAAAA";
    private static final String UPDATED_ID_F = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private WorkflowRepository workflowRepository;

    /**
     * This repository is mocked in the fr.fastmarketeam.pimnow.repository.search test package.
     *
     * @see fr.fastmarketeam.pimnow.repository.search.WorkflowSearchRepositoryMockConfiguration
     */
    @Autowired
    private WorkflowSearchRepository mockWorkflowSearchRepository;

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

    private MockMvc restWorkflowMockMvc;

    private Workflow workflow;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final WorkflowResource workflowResource = new WorkflowResource(workflowRepository, mockWorkflowSearchRepository);
        this.restWorkflowMockMvc = MockMvcBuilders.standaloneSetup(workflowResource)
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
    public static Workflow createEntity(EntityManager em) {
        Workflow workflow = new Workflow()
            .idF(DEFAULT_ID_F)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION);
        return workflow;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Workflow createUpdatedEntity(EntityManager em) {
        Workflow workflow = new Workflow()
            .idF(UPDATED_ID_F)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION);
        return workflow;
    }

    @BeforeEach
    public void initTest() {
        workflow = createEntity(em);
    }

    @Test
    @Transactional
    public void createWorkflow() throws Exception {
        int databaseSizeBeforeCreate = workflowRepository.findAll().size();

        // Create the Workflow
        restWorkflowMockMvc.perform(post("/api/workflows")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflow)))
            .andExpect(status().isCreated());

        // Validate the Workflow in the database
        List<Workflow> workflowList = workflowRepository.findAll();
        assertThat(workflowList).hasSize(databaseSizeBeforeCreate + 1);
        Workflow testWorkflow = workflowList.get(workflowList.size() - 1);
        assertThat(testWorkflow.getIdF()).isEqualTo(DEFAULT_ID_F);
        assertThat(testWorkflow.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWorkflow.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Workflow in Elasticsearch
        verify(mockWorkflowSearchRepository, times(1)).save(testWorkflow);
    }

    @Test
    @Transactional
    public void createWorkflowWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = workflowRepository.findAll().size();

        // Create the Workflow with an existing ID
        workflow.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkflowMockMvc.perform(post("/api/workflows")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflow)))
            .andExpect(status().isBadRequest());

        // Validate the Workflow in the database
        List<Workflow> workflowList = workflowRepository.findAll();
        assertThat(workflowList).hasSize(databaseSizeBeforeCreate);

        // Validate the Workflow in Elasticsearch
        verify(mockWorkflowSearchRepository, times(0)).save(workflow);
    }


    @Test
    @Transactional
    public void checkIdFIsRequired() throws Exception {
        int databaseSizeBeforeTest = workflowRepository.findAll().size();
        // set the field null
        workflow.setIdF(null);

        // Create the Workflow, which fails.

        restWorkflowMockMvc.perform(post("/api/workflows")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflow)))
            .andExpect(status().isBadRequest());

        List<Workflow> workflowList = workflowRepository.findAll();
        assertThat(workflowList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = workflowRepository.findAll().size();
        // set the field null
        workflow.setName(null);

        // Create the Workflow, which fails.

        restWorkflowMockMvc.perform(post("/api/workflows")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflow)))
            .andExpect(status().isBadRequest());

        List<Workflow> workflowList = workflowRepository.findAll();
        assertThat(workflowList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWorkflows() throws Exception {
        // Initialize the database
        workflowRepository.saveAndFlush(workflow);

        // Get all the workflowList
        restWorkflowMockMvc.perform(get("/api/workflows?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workflow.getId().intValue())))
            .andExpect(jsonPath("$.[*].idF").value(hasItem(DEFAULT_ID_F.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
    
    @Test
    @Transactional
    public void getWorkflow() throws Exception {
        // Initialize the database
        workflowRepository.saveAndFlush(workflow);

        // Get the workflow
        restWorkflowMockMvc.perform(get("/api/workflows/{id}", workflow.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(workflow.getId().intValue()))
            .andExpect(jsonPath("$.idF").value(DEFAULT_ID_F.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingWorkflow() throws Exception {
        // Get the workflow
        restWorkflowMockMvc.perform(get("/api/workflows/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWorkflow() throws Exception {
        // Initialize the database
        workflowRepository.saveAndFlush(workflow);

        int databaseSizeBeforeUpdate = workflowRepository.findAll().size();

        // Update the workflow
        Workflow updatedWorkflow = workflowRepository.findById(workflow.getId()).get();
        // Disconnect from session so that the updates on updatedWorkflow are not directly saved in db
        em.detach(updatedWorkflow);
        updatedWorkflow
            .idF(UPDATED_ID_F)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION);

        restWorkflowMockMvc.perform(put("/api/workflows")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWorkflow)))
            .andExpect(status().isOk());

        // Validate the Workflow in the database
        List<Workflow> workflowList = workflowRepository.findAll();
        assertThat(workflowList).hasSize(databaseSizeBeforeUpdate);
        Workflow testWorkflow = workflowList.get(workflowList.size() - 1);
        assertThat(testWorkflow.getIdF()).isEqualTo(UPDATED_ID_F);
        assertThat(testWorkflow.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWorkflow.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Workflow in Elasticsearch
        verify(mockWorkflowSearchRepository, times(1)).save(testWorkflow);
    }

    @Test
    @Transactional
    public void updateNonExistingWorkflow() throws Exception {
        int databaseSizeBeforeUpdate = workflowRepository.findAll().size();

        // Create the Workflow

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkflowMockMvc.perform(put("/api/workflows")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflow)))
            .andExpect(status().isBadRequest());

        // Validate the Workflow in the database
        List<Workflow> workflowList = workflowRepository.findAll();
        assertThat(workflowList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Workflow in Elasticsearch
        verify(mockWorkflowSearchRepository, times(0)).save(workflow);
    }

    @Test
    @Transactional
    public void deleteWorkflow() throws Exception {
        // Initialize the database
        workflowRepository.saveAndFlush(workflow);

        int databaseSizeBeforeDelete = workflowRepository.findAll().size();

        // Delete the workflow
        restWorkflowMockMvc.perform(delete("/api/workflows/{id}", workflow.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Workflow> workflowList = workflowRepository.findAll();
        assertThat(workflowList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Workflow in Elasticsearch
        verify(mockWorkflowSearchRepository, times(1)).deleteById(workflow.getId());
    }

    @Test
    @Transactional
    public void searchWorkflow() throws Exception {
        // Initialize the database
        workflowRepository.saveAndFlush(workflow);
        when(mockWorkflowSearchRepository.search(queryStringQuery("id:" + workflow.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(workflow), PageRequest.of(0, 1), 1));
        // Search the workflow
        restWorkflowMockMvc.perform(get("/api/_search/workflows?query=id:" + workflow.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workflow.getId().intValue())))
            .andExpect(jsonPath("$.[*].idF").value(hasItem(DEFAULT_ID_F)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Workflow.class);
        Workflow workflow1 = new Workflow();
        workflow1.setId(1L);
        Workflow workflow2 = new Workflow();
        workflow2.setId(workflow1.getId());
        assertThat(workflow1).isEqualTo(workflow2);
        workflow2.setId(2L);
        assertThat(workflow1).isNotEqualTo(workflow2);
        workflow1.setId(null);
        assertThat(workflow1).isNotEqualTo(workflow2);
    }
}
