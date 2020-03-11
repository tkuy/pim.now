package fr.fastmarketeam.pimnow.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.fastmarketeam.pimnow.domain.enumeration.EnumWorkflowState;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A WorkflowState.
 */
@Entity
@Table(name = "workflow_state")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "workflowstate")
public class WorkflowState implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private EnumWorkflowState state;

    @Column(name = "fail_description")
    private String failDescription;

    @OneToMany(mappedBy = "workflowState")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<WorkflowStep> steps = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("workflowStates")
    private Product product;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnumWorkflowState getState() {
        return state;
    }

    public WorkflowState state(EnumWorkflowState state) {
        this.state = state;
        return this;
    }

    public void setState(EnumWorkflowState state) {
        this.state = state;
    }

    public String getFailDescription() {
        return failDescription;
    }

    public WorkflowState failDescription(String failDescription) {
        this.failDescription = failDescription;
        return this;
    }

    public void setFailDescription(String failDescription) {
        this.failDescription = failDescription;
    }

    public Set<WorkflowStep> getSteps() {
        return steps;
    }

    public WorkflowState steps(Set<WorkflowStep> workflowSteps) {
        this.steps = workflowSteps;
        return this;
    }

    public WorkflowState addStep(WorkflowStep workflowStep) {
        this.steps.add(workflowStep);
        workflowStep.setWorkflowState(this);
        return this;
    }

    public WorkflowState removeStep(WorkflowStep workflowStep) {
        this.steps.remove(workflowStep);
        workflowStep.setWorkflowState(null);
        return this;
    }

    public void setSteps(Set<WorkflowStep> workflowSteps) {
        this.steps = workflowSteps;
    }

    public Product getProduct() {
        return product;
    }

    public WorkflowState product(Product product) {
        this.product = product;
        return this;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkflowState)) {
            return false;
        }
        return id != null && id.equals(((WorkflowState) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "WorkflowState{" +
            "id=" + getId() +
            ", state='" + getState() + "'" +
            ", failDescription='" + getFailDescription() + "'" +
            "}";
    }
}
