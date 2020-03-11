import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, InputGroup, Row, Table} from 'reactstrap';
import {AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntities, getSearchEntities} from './values-list-item.reducer';

export interface IValuesListItemProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export interface IValuesListItemState {
  search: string;
}

export class ValuesListItem extends React.Component<IValuesListItemProps, IValuesListItemState> {
  state: IValuesListItemState = {
    search: ''
  };

  componentDidMount() {
    this.props.getEntities();
  }

  render() {
    const { valuesListItemList, match } = this.props;
    return (
      <div>
        <h2 id="values-list-item-heading">
          <Translate contentKey="pimnowApp.valuesListItem.home.title">Values List Items</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="pimnowApp.valuesListItem.home.createLabel">Create a new Values List Item</Translate>
          </Link>
        </h2>
        <br/>
        <div className="table-responsive">
          {valuesListItemList && valuesListItemList.length > 0 ? (
            <Table responsive aria-describedby="values-list-item-heading">
              <thead>
                <tr>
                  <th>
                    <Translate contentKey="global.field.id">ID</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.valuesListItem.value">Value</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.valuesListItem.valuesList">Values List</Translate>
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {valuesListItemList.map((valuesListItem, i) => (
                  <tr key={`entity-${i}`}>
                    <td>
                      <Button tag={Link} to={`${match.url}/${valuesListItem.id}`} color="link" size="sm">
                        {valuesListItem.id}
                      </Button>
                    </td>
                    <td>{valuesListItem.value}</td>
                    <td>
                      {valuesListItem.valuesList ? (
                        <Link to={`values-list/${valuesListItem.valuesList.id}`}>{valuesListItem.valuesList.id}</Link>
                      ) : (
                        ''
                      )}
                    </td>
                    <td className="text-right">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`${match.url}/${valuesListItem.id}`} color="info" size="sm">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${valuesListItem.id}/edit`} color="primary" size="sm">
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${valuesListItem.id}/delete`} color="danger" size="sm">
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
              <Translate contentKey="pimnowApp.valuesListItem.home.notFound">No Values List Items found</Translate>
            </div>
          )}
        </div>
      </div>
    );
  }
}

const mapStateToProps = ({ valuesListItem }: IRootState) => ({
  valuesListItemList: valuesListItem.entities
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
)(ValuesListItem);
