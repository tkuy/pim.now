import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Label, Row} from 'reactstrap';
import {AvField, AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {IRootState} from 'app/shared/reducers';
import {getEntities as getWorkflows} from 'app/entities/workflow/workflow.reducer';
import {getEntities as getWorkflowSteps} from 'app/entities/workflow-step/workflow-step.reducer';
import {getEntities as getWorkflowStates} from 'app/entities/workflow-state/workflow-state.reducer';
import {createEntity, getEntity, reset, updateEntity} from './workflow-step.reducer';

export interface IWorkflowStepUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IWorkflowStepUpdateState {
  isNew: boolean;
  workflowId: string;
  successorId: string;
  workflowStateId: string;
}

export class WorkflowStepUpdate extends React.Component<IWorkflowStepUpdateProps, IWorkflowStepUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      workflowId: '0',
      successorId: '0',
      workflowStateId: '0',
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    if (this.state.isNew) {
      this.props.reset();
    } else {
      this.props.getEntity(this.props.match.params.id);
    }

    this.props.getWorkflows();
    this.props.getWorkflowSteps();
    this.props.getWorkflowStates();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { workflowStepEntity } = this.props;
      const entity = {
        ...workflowStepEntity,
        ...values
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/workflow-step');
  };

  render() {
    const { workflowStepEntity, workflows, workflowSteps, workflowStates, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="pimnowApp.workflowStep.home.createOrEditLabel">
              <Translate contentKey="pimnowApp.workflowStep.home.createOrEditLabel">Create or edit a WorkflowStep</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : workflowStepEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="workflow-step-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="workflow-step-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="idFLabel" for="workflow-step-idF">
                    <Translate contentKey="pimnowApp.workflowStep.idF">Id F</Translate>
                  </Label>
                  <AvField
                    id="workflow-step-idF"
                    type="text"
                    name="idF"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="nameLabel" for="workflow-step-name">
                    <Translate contentKey="pimnowApp.workflowStep.name">Name</Translate>
                  </Label>
                  <AvField
                    id="workflow-step-name"
                    type="text"
                    name="name"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="descriptionLabel" for="workflow-step-description">
                    <Translate contentKey="pimnowApp.workflowStep.description">Description</Translate>
                  </Label>
                  <AvField id="workflow-step-description" type="text" name="description" />
                </AvGroup>
                <AvGroup>
                  <Label id="rankLabel" for="workflow-step-rank">
                    <Translate contentKey="pimnowApp.workflowStep.rank">Rank</Translate>
                  </Label>
                  <AvField
                    id="workflow-step-rank"
                    type="string"
                    className="form-control"
                    name="rank"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="isIntegrationStepLabel" check>
                    <AvInput id="workflow-step-isIntegrationStep" type="checkbox" className="form-control" name="isIntegrationStep" />
                    <Translate contentKey="pimnowApp.workflowStep.isIntegrationStep">Is Integration Step</Translate>
                  </Label>
                </AvGroup>
                <AvGroup>
                  <Label for="workflow-step-workflow">
                    <Translate contentKey="pimnowApp.workflowStep.workflow">Workflow</Translate>
                  </Label>
                  <AvInput id="workflow-step-workflow" type="select" className="form-control" name="workflow.id">
                    <option value="" key="0" />
                    {workflows
                      ? workflows.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="workflow-step-successor">
                    <Translate contentKey="pimnowApp.workflowStep.successor">Successor</Translate>
                  </Label>
                  <AvInput id="workflow-step-successor" type="select" className="form-control" name="successor.id">
                    <option value="" key="0" />
                    {workflowSteps
                      ? workflowSteps.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="workflow-step-workflowState">
                    <Translate contentKey="pimnowApp.workflowStep.workflowState">Workflow State</Translate>
                  </Label>
                  <AvInput id="workflow-step-workflowState" type="select" className="form-control" name="workflowState.id">
                    <option value="" key="0" />
                    {workflowStates
                      ? workflowStates.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/workflow-step" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" />
                  &nbsp;
                  <span className="d-none d-md-inline">
                    <Translate contentKey="entity.action.back">Back</Translate>
                  </span>
                </Button>
                &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                  <FontAwesomeIcon icon="save" />
                  &nbsp;
                  <Translate contentKey="entity.action.save">Save</Translate>
                </Button>
              </AvForm>
            )}
          </Col>
        </Row>
      </div>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  workflows: storeState.workflow.entities,
  workflowSteps: storeState.workflowStep.entities,
  workflowStates: storeState.workflowState.entities,
  workflowStepEntity: storeState.workflowStep.entity,
  loading: storeState.workflowStep.loading,
  updating: storeState.workflowStep.updating,
  updateSuccess: storeState.workflowStep.updateSuccess
});

const mapDispatchToProps = {
  getWorkflows,
  getWorkflowSteps,
  getWorkflowStates,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(WorkflowStepUpdate);
