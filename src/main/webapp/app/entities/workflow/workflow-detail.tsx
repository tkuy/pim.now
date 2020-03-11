import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './workflow.reducer';

export interface IWorkflowDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class WorkflowDetail extends React.Component<IWorkflowDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { workflowEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.workflow.detail.title">Workflow</Translate> [<b>{workflowEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="idF">
                <Translate contentKey="pimnowApp.workflow.idF">Id F</Translate>
              </span>
            </dt>
            <dd>{workflowEntity.idF}</dd>
            <dt>
              <span id="name">
                <Translate contentKey="pimnowApp.workflow.name">Name</Translate>
              </span>
            </dt>
            <dd>{workflowEntity.name}</dd>
            <dt>
              <span id="description">
                <Translate contentKey="pimnowApp.workflow.description">Description</Translate>
              </span>
            </dt>
            <dd>{workflowEntity.description}</dd>
            <dt>
              <Translate contentKey="pimnowApp.workflow.customer">Customer</Translate>
            </dt>
            <dd>{workflowEntity.customer ? workflowEntity.customer.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/workflow" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/workflow/${workflowEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ workflow }: IRootState) => ({
  workflowEntity: workflow.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(WorkflowDetail);
