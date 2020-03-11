import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './workflow-state.reducer';

export interface IWorkflowStateDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class WorkflowStateDetail extends React.Component<IWorkflowStateDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { workflowStateEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.workflowState.detail.title">WorkflowState</Translate> [<b>{workflowStateEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="state">
                <Translate contentKey="pimnowApp.workflowState.state">State</Translate>
              </span>
            </dt>
            <dd>{workflowStateEntity.state}</dd>
            <dt>
              <span id="failDescription">
                <Translate contentKey="pimnowApp.workflowState.failDescription">Fail Description</Translate>
              </span>
            </dt>
            <dd>{workflowStateEntity.failDescription}</dd>
            <dt>
              <Translate contentKey="pimnowApp.workflowState.product">Product</Translate>
            </dt>
            <dd>{workflowStateEntity.product ? workflowStateEntity.product.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/workflow-state" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/workflow-state/${workflowStateEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ workflowState }: IRootState) => ({
  workflowStateEntity: workflowState.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(WorkflowStateDetail);
