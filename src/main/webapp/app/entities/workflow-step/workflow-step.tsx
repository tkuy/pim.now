import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, InputGroup, Row, Table} from 'reactstrap';
import {AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntities, getSearchEntities} from './workflow-step.reducer';

export interface IWorkflowStepProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export interface IWorkflowStepState {
  search: string;
}

export class WorkflowStep extends React.Component<IWorkflowStepProps, IWorkflowStepState> {
  state: IWorkflowStepState = {
    search: ''
  };

  componentDidMount() {
    this.props.getEntities();
  }


  render() {
    const { workflowStepList, match } = this.props;
    return (
      <div>
        <h2 id="workflow-step-heading">
          <Translate contentKey="pimnowApp.workflowStep.home.title">Workflow Steps</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="pimnowApp.workflowStep.home.createLabel">Create a new Workflow Step</Translate>
          </Link>
        </h2>
        <br/>
        <div className="table-responsive">
          {workflowStepList && workflowStepList.length > 0 ? (
            <Table responsive aria-describedby="workflow-step-heading">
              <thead>
                <tr>
                  <th>
                    <Translate contentKey="global.field.id">ID</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.workflowStep.idF">Id F</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.workflowStep.name">Name</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.workflowStep.description">Description</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.workflowStep.rank">Rank</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.workflowStep.isIntegrationStep">Is Integration Step</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.workflowStep.workflow">Workflow</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.workflowStep.successor">Successor</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.workflowStep.workflowState">Workflow State</Translate>
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {workflowStepList.map((workflowStep, i) => (
                  <tr key={`entity-${i}`}>
                    <td>
                      <Button tag={Link} to={`${match.url}/${workflowStep.id}`} color="link" size="sm">
                        {workflowStep.id}
                      </Button>
                    </td>
                    <td>{workflowStep.idF}</td>
                    <td>{workflowStep.name}</td>
                    <td>{workflowStep.description}</td>
                    <td>{workflowStep.rank}</td>
                    <td>{workflowStep.isIntegrationStep ? 'true' : 'false'}</td>
                    <td>
                      {workflowStep.workflow ? <Link to={`workflow/${workflowStep.workflow.id}`}>{workflowStep.workflow.id}</Link> : ''}
                    </td>
                    <td>
                      {workflowStep.successor ? (
                        <Link to={`workflow-step/${workflowStep.successor.id}`}>{workflowStep.successor.id}</Link>
                      ) : (
                        ''
                      )}
                    </td>
                    <td>
                      {workflowStep.workflowState ? (
                        <Link to={`workflow-state/${workflowStep.workflowState.id}`}>{workflowStep.workflowState.id}</Link>
                      ) : (
                        ''
                      )}
                    </td>
                    <td className="text-right">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`${match.url}/${workflowStep.id}`} color="info" size="sm">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${workflowStep.id}/edit`} color="primary" size="sm">
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${workflowStep.id}/delete`} color="danger" size="sm">
                          <FontAwesomeIcon icon="trash" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.delete">Delete</Translate>
                          </span>
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            <div className="alert alert-warning">
              <Translate contentKey="pimnowApp.workflowStep.home.notFound">No Workflow Steps found</Translate>
            </div>
          )}
        </div>
      </div>
    );
  }
}

const mapStateToProps = ({ workflowStep }: IRootState) => ({
  workflowStepList: workflowStep.entities
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(WorkflowStep);
