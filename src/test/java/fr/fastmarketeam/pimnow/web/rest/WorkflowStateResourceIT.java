package fr.fastmarketeam.pimnow.web.rest;

import fr.fastmarketeam.pimnow.PimnowApp;
import fr.fastmarketeam.pimnow.domain.WorkflowState;
import fr.fastmarketeam.pimnow.repository.WorkflowStateRepository;
import fr.fastmarketeam.pimnow.repository.search.WorkflowStateSearchRepository;
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

import fr.fastmarketeam.pimnow.domain.enumeration.EnumWorkflowState;
/**
 * Integration tests for the {@link WorkflowStateResource} REST controller.
 */
@SpringBootTest(classes = PimnowApp.class)
public class WorkflowStateResourceIT {

    private static final EnumWorkflowState DEFAULT_STATE = EnumWorkflowState.IN_PROGRESS;
    private static final EnumWorkflowState UPDATED_STATE = EnumWorkflowState.REFUSED;

    private static final String DEFAULT_FAIL_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_FAIL_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private WorkflowStateRepository workflowStateRepository;

    /**
     * This repository is mocked in the fr.fastmarketeam.pimnow.repository.search test package.
     *
     * @see fr.fastmarketeam.pimnow.repository.search.WorkflowStateSearchRepositoryMockConfiguration
     */
    @Autowired
    private WorkflowStateSearchRepository mockWorkflowStateSearchRepository;

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

    private MockMvc restWorkflowStateMockMvc;

    private WorkflowState workflowState;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final WorkflowStateResource workflowStateResource = new WorkflowStateResource(workflowStateRepository, mockWorkflowStateSearchRepository);
        this.restWorkflowStateMockMvc = MockMvcBuilders.standaloneSetup(workflowStateResource)
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
    public static WorkflowState createEntity(EntityManager em) {
        WorkflowState workflowState = new WorkflowState()
            .state(DEFAULT_STATE)
            .failDescription(DEFAULT_FAIL_DESCRIPTION);
        return workflowState;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkflowState createUpdatedEntity(EntityManager em) {
        WorkflowState workflowState = new WorkflowState()
            .state(UPDATED_STATE)
            .failDescription(UPDATED_FAIL_DESCRIPTION);
        return workflowState;
    }

    @BeforeEach
    public void initTest() {
        workflowState = createEntity(em);
    }

    @Test
    @Transactional
    public void createWorkflowState() throws Exception {
        int databaseSizeBeforeCreate = workflowStateRepository.findAll().size();

        // Create the WorkflowState
        restWorkflowStateMockMvc.perform(post("/api/workflow-states")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflowState)))
            .andExpect(status().isCreated());

        // Validate the WorkflowState in the database
        List<WorkflowState> workflowStateList = workflowStateRepository.findAll();
        assertThat(workflowStateList).hasSize(databaseSizeBeforeCreate + 1);
        WorkflowState testWorkflowState = workflowStateList.get(workflowStateList.size() - 1);
        assertThat(testWorkflowState.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testWorkflowState.getFailDescription()).isEqualTo(DEFAULT_FAIL_DESCRIPTION);

        // Validate the WorkflowState in Elasticsearch
        verify(mockWorkflowStateSearchRepository, times(1)).save(testWorkflowState);
    }

    @Test
    @Transactional
    public void createWorkflowStateWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = workflowStateRepository.findAll().size();

        // Create the WorkflowState with an existing ID
        workflowState.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkflowStateMockMvc.perform(post("/api/workflow-states")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflowState)))
            .andExpect(status().isBadRequest());

        // Validate the WorkflowState in the database
        List<WorkflowState> workflowStateList = workflowStateRepository.findAll();
        assertThat(workflowStateList).hasSize(databaseSizeBeforeCreate);

        // Validate the WorkflowState in Elasticsearch
        verify(mockWorkflowStateSearchRepository, times(0)).save(workflowState);
    }


    @Test
    @Transactional
    public void checkStateIsRequired() throws Exception {
        int databaseSizeBeforeTest = workflowStateRepository.findAll().size();
        // set the field null
        workflowState.setState(null);

        // Create the WorkflowState, which fails.

        restWorkflowStateMockMvc.perform(post("/api/workflow-states")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflowState)))
            .andExpect(status().isBadRequest());

        List<WorkflowState> workflowStateList = workflowStateRepository.findAll();
        assertThat(workflowStateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWorkflowStates() throws Exception {
        // Initialize the database
        workflowStateRepository.saveAndFlush(workflowState);

        // Get all the workflowStateList
        restWorkflowStateMockMvc.perform(get("/api/workflow-states?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workflowState.getId().intValue())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].failDescription").value(hasItem(DEFAULT_FAIL_DESCRIPTION.toString())));
    }
    
    @Test
    @Transactional
    public void getWorkflowState() throws Exception {
        // Initialize the database
        workflowStateRepository.saveAndFlush(workflowState);

        // Get the workflowState
        restWorkflowStateMockMvc.perform(get("/api/workflow-states/{id}", workflowState.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(workflowState.getId().intValue()))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.failDescription").value(DEFAULT_FAIL_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingWorkflowState() throws Exception {
        // Get the workflowState
        restWorkflowStateMockMvc.perform(get("/api/workflow-states/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWorkflowState() throws Exception {
        // Initialize the database
        workflowStateRepository.saveAndFlush(workflowState);

        int databaseSizeBeforeUpdate = workflowStateRepository.findAll().size();

        // Update the workflowState
        WorkflowState updatedWorkflowState = workflowStateRepository.findById(workflowState.getId()).get();
        // Disconnect from session so that the updates on updatedWorkflowState are not directly saved in db
        em.detach(updatedWorkflowState);
        updatedWorkflowState
            .state(UPDATED_STATE)
            .failDescription(UPDATED_FAIL_DESCRIPTION);

        restWorkflowStateMockMvc.perform(put("/api/workflow-states")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWorkflowState)))
            .andExpect(status().isOk());

        // Validate the WorkflowState in the database
        List<WorkflowState> workflowStateList = workflowStateRepository.findAll();
        assertThat(workflowStateList).hasSize(databaseSizeBeforeUpdate);
        WorkflowState testWorkflowState = workflowStateList.get(workflowStateList.size() - 1);
        assertThat(testWorkflowState.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testWorkflowState.getFailDescription()).isEqualTo(UPDATED_FAIL_DESCRIPTION);

        // Validate the WorkflowState in Elasticsearch
        verify(mockWorkflowStateSearchRepository, times(1)).save(testWorkflowState);
    }

    @Test
    @Transactional
    public void updateNonExistingWorkflowState() throws Exception {
        int databaseSizeBeforeUpdate = workflowStateRepository.findAll().size();

        // Create the WorkflowState

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkflowStateMockMvc.perform(put("/api/workflow-states")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(workflowState)))
            .andExpect(status().isBadRequest());

        // Validate the WorkflowState in the database
        List<WorkflowState> workflowStateList = workflowStateRepository.findAll();
        assertThat(workflowStateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WorkflowState in Elasticsearch
        verify(mockWorkflowStateSearchRepository, times(0)).save(workflowState);
    }

    @Test
    @Transactional
    public void deleteWorkflowState() throws Exception {
        // Initialize the database
        workflowStateRepository.saveAndFlush(workflowState);

        int databaseSizeBeforeDelete = workflowStateRepository.findAll().size();

        // Delete the workflowState
        restWorkflowStateMockMvc.perform(delete("/api/workflow-states/{id}", workflowState.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WorkflowState> workflowStateList = workflowStateRepository.findAll();
        assertThat(workflowStateList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the WorkflowState in Elasticsearch
        verify(mockWorkflowStateSearchRepository, times(1)).deleteById(workflowState.getId());
    }

    @Test
    @Transactional
    public void searchWorkflowState() throws Exception {
        // Initialize the database
        workflowStateRepository.saveAndFlush(workflowState);
        when(mockWorkflowStateSearchRepository.search(queryStringQuery("id:" + workflowState.getId())))
            .thenReturn(Collections.singletonList(workflowState));
        // Search the workflowState
        restWorkflowStateMockMvc.perform(get("/api/_search/workflow-states?query=id:" + workflowState.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workflowState.getId().intValue())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].failDescription").value(hasItem(DEFAULT_FAIL_DESCRIPTION)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkflowState.class);
        WorkflowState workflowState1 = new WorkflowState();
        workflowState1.setId(1L);
        WorkflowState workflowState2 = new WorkflowState();
        workflowState2.setId(workflowState1.getId());
        assertThat(workflowState1).isEqualTo(workflowState2);
        workflowState2.setId(2L);
        assertThat(workflowState1).isNotEqualTo(workflowState2);
        workflowState1.setId(null);
        assertThat(workflowState1).isNotEqualTo(workflowState2);
    }
}
