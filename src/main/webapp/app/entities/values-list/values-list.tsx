import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, InputGroup, Row, Table} from 'reactstrap';
import {AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntities, getSearchEntities} from './values-list.reducer';

export interface IValuesListProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export interface IValuesListState {
  search: string;
}

export class ValuesList extends React.Component<IValuesListProps, IValuesListState> {
  state: IValuesListState = {
    search: ''
  };

  componentDidMount() {
    this.props.getEntities();
  }

  render() {
    const { valuesListList, match } = this.props;
    return (
      <div>
        <h2 id="values-list-heading">
          <Translate contentKey="pimnowApp.valuesList.home.title">Values Lists</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="pimnowApp.valuesList.home.createLabel">Create a new Values List</Translate>
          </Link>
        </h2>
        <br/>
        <div className="table-responsive">
          {valuesListList && valuesListList.length > 0 ? (
            <Table responsive aria-describedby="values-list-heading">
              <thead>
                <tr>
                  <th>
                    <Translate contentKey="global.field.id">ID</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.valuesList.idF">Id F</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.valuesList.customer">Customer</Translate>
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {valuesListList.map((valuesList, i) => (
                  <tr key={`entity-${i}`}>
                    <td>
                      <Button tag={Link} to={`${match.url}/${valuesList.id}`} color="link" size="sm">
                        {valuesList.id}
                      </Button>
                    </td>
                    <td>{valuesList.idF}</td>
                    <td>{valuesList.customer ? <Link to={`customer/${valuesList.customer.id}`}>{valuesList.customer.id}</Link> : ''}</td>
                    <td className="text-right">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`${match.url}/${valuesList.id}`} color="info" size="sm">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${valuesList.id}/edit`} color="primary" size="sm">
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${valuesList.id}/delete`} color="danger" size="sm">
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
              <Translate contentKey="pimnowApp.valuesList.home.notFound">No Values Lists found</Translate>
            </div>
          )}
        </div>
      </div>
    );
  }
}

const mapStateToProps = ({ valuesList }: IRootState) => ({
  valuesListList: valuesList.entities
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
)(ValuesList);
