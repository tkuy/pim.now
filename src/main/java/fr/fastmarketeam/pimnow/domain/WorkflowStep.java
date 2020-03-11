package fr.fastmarketeam.pimnow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A WorkflowStep.
 */
@Entity
@Table(name = "workflow_step")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "workflowstep")
public class WorkflowStep implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "id_f", nullable = false)
    private String idF;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "rank", nullable = false)
    private Integer rank;

    @NotNull
    @Column(name = "is_integration_step", nullable = false)
    private Boolean isIntegrationStep;

    @ManyToOne
    @JsonIgnoreProperties("workflowSteps")
    private Workflow workflow;

    @OneToOne
    @JoinColumn(unique = true)
    private WorkflowStep successor;

    @ManyToOne
    @JsonIgnoreProperties("workflowSteps")
    private WorkflowState workflowState;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdF() {
        return idF;
    }

    public WorkflowStep idF(String idF) {
        this.idF = idF;
        return this;
    }

    public void setIdF(String idF) {
        this.idF = idF;
    }

    public String getName() {
        return name;
    }

    public WorkflowStep name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public WorkflowStep description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRank() {
        return rank;
    }

    public WorkflowStep rank(Integer rank) {
        this.rank = rank;
        return this;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Boolean isIsIntegrationStep() {
        return isIntegrationStep;
    }

    public WorkflowStep isIntegrationStep(Boolean isIntegrationStep) {
        this.isIntegrationStep = isIntegrationStep;
        return this;
    }

    public void setIsIntegrationStep(Boolean isIntegrationStep) {
        this.isIntegrationStep = isIntegrationStep;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public WorkflowStep workflow(Workflow workflow) {
        this.workflow = workflow;
        return this;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public WorkflowStep getSuccessor() {
        return successor;
    }

    public WorkflowStep successor(WorkflowStep workflowStep) {
        this.successor = workflowStep;
        return this;
    }

    public void setSuccessor(WorkflowStep workflowStep) {
        this.successor = workflowStep;
    }

    public WorkflowState getWorkflowState() {
        return workflowState;
    }

    public WorkflowStep workflowState(WorkflowState workflowState) {
        this.workflowState = workflowState;
        return this;
    }

    public void setWorkflowState(WorkflowState workflowState) {
        this.workflowState = workflowState;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkflowStep)) {
            return false;
        }
        return id != null && id.equals(((WorkflowStep) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "WorkflowStep{" +
            "id=" + getId() +
            ", idF='" + getIdF() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", rank=" + getRank() +
            ", isIntegrationStep='" + isIsIntegrationStep() + "'" +
            "}";
    }
}
