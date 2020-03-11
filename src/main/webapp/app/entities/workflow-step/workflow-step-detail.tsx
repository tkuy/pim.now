import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './workflow-step.reducer';

export interface IWorkflowStepDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class WorkflowStepDetail extends React.Component<IWorkflowStepDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { workflowStepEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.workflowStep.detail.title">WorkflowStep</Translate> [<b>{workflowStepEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="idF">
                <Translate contentKey="pimnowApp.workflowStep.idF">Id F</Translate>
              </span>
            </dt>
            <dd>{workflowStepEntity.idF}</dd>
            <dt>
              <span id="name">
                <Translate contentKey="pimnowApp.workflowStep.name">Name</Translate>
              </span>
            </dt>
            <dd>{workflowStepEntity.name}</dd>
            <dt>
              <span id="description">
                <Translate contentKey="pimnowApp.workflowStep.description">Description</Translate>
              </span>
            </dt>
            <dd>{workflowStepEntity.description}</dd>
            <dt>
              <span id="rank">
                <Translate contentKey="pimnowApp.workflowStep.rank">Rank</Translate>
              </span>
            </dt>
            <dd>{workflowStepEntity.rank}</dd>
            <dt>
              <span id="isIntegrationStep">
                <Translate contentKey="pimnowApp.workflowStep.isIntegrationStep">Is Integration Step</Translate>
              </span>
            </dt>
            <dd>{workflowStepEntity.isIntegrationStep ? 'true' : 'false'}</dd>
            <dt>
              <Translate contentKey="pimnowApp.workflowStep.workflow">Workflow</Translate>
            </dt>
            <dd>{workflowStepEntity.workflow ? workflowStepEntity.workflow.id : ''}</dd>
            <dt>
              <Translate contentKey="pimnowApp.workflowStep.successor">Successor</Translate>
            </dt>
            <dd>{workflowStepEntity.successor ? workflowStepEntity.successor.id : ''}</dd>
            <dt>
              <Translate contentKey="pimnowApp.workflowStep.workflowState">Workflow State</Translate>
            </dt>
            <dd>{workflowStepEntity.workflowState ? workflowStepEntity.workflowState.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/workflow-step" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/workflow-step/${workflowStepEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.edit">Edit</Translate>
            </span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ workflowStep }: IRootState) => ({
  workflowStepEntity: workflowStep.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(WorkflowStepDetail);
