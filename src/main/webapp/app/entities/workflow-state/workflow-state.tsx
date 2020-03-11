import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, InputGroup, Row, Table} from 'reactstrap';
import {AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntities, getSearchEntities} from './workflow-state.reducer';

export interface IWorkflowStateProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export interface IWorkflowStateState {
  search: string;
}

export class WorkflowState extends React.Component<IWorkflowStateProps, IWorkflowStateState> {
  state: IWorkflowStateState = {
    search: ''
  };

  componentDidMount() {
    this.props.getEntities();
  }


  render() {
    const { workflowStateList, match } = this.props;
    return (
      <div>
        <h2 id="workflow-state-heading">
          <Translate contentKey="pimnowApp.workflowState.home.title">Workflow States</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="pimnowApp.workflowState.home.createLabel">Create a new Workflow State</Translate>
          </Link>
        </h2>
        <br/>
        <div className="table-responsive">
          {workflowStateList && workflowStateList.length > 0 ? (
            <Table responsive aria-describedby="workflow-state-heading">
              <thead>
                <tr>
                  <th>
                    <Translate contentKey="global.field.id">ID</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.workflowState.state">State</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.workflowState.failDescription">Fail Description</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.workflowState.product">Product</Translate>
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {workflowStateList.map((workflowState, i) => (
                  <tr key={`entity-${i}`}>
                    <td>
                      <Button tag={Link} to={`${match.url}/${workflowState.id}`} color="link" size="sm">
                        {workflowState.id}
                      </Button>
                    </td>
                    <td>
                      <Translate contentKey={`pimnowApp.EnumWorkflowState.${workflowState.state}`} />
                    </td>
                    <td>{workflowState.failDescription}</td>
                    <td>
                      {workflowState.product ? <Link to={`product/${workflowState.product.id}`}>{workflowState.product.id}</Link> : ''}
                    </td>
                    <td className="text-right">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`${match.url}/${workflowState.id}`} color="info" size="sm">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${workflowState.id}/edit`} color="primary" size="sm">
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${workflowState.id}/delete`} color="danger" size="sm">
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
              <Translate contentKey="pimnowApp.workflowState.home.notFound">No Workflow States found</Translate>
            </div>
          )}
        </div>
      </div>
    );
  }
}

const mapStateToProps = ({ workflowState }: IRootState) => ({
  workflowStateList: workflowState.entities
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
)(WorkflowState);
