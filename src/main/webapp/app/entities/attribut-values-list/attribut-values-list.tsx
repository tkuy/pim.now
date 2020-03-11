import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, InputGroup, Row, Table} from 'reactstrap';
import {AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntities, getSearchEntities} from './attribut-values-list.reducer';

export interface IAttributValuesListProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export interface IAttributValuesListState {
  search: string;
}

export class AttributValuesList extends React.Component<IAttributValuesListProps, IAttributValuesListState> {
  state: IAttributValuesListState = {
    search: ''
  };

  componentDidMount() {
    this.props.getEntities();
  }


  render() {
    const { attributValuesListList, match } = this.props;
    return (
      <div>
        <h2 id="attribut-values-list-heading">
          <Translate contentKey="pimnowApp.attributValuesList.home.title">Attribut Values Lists</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="pimnowApp.attributValuesList.home.createLabel">Create a new Attribut Values List</Translate>
          </Link>
        </h2>
        <br/>
        <div className="table-responsive">
          {attributValuesListList && attributValuesListList.length > 0 ? (
            <Table responsive aria-describedby="attribut-values-list-heading">
              <thead>
                <tr>
                  <th>
                    <Translate contentKey="global.field.id">ID</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.attributValuesList.attribut">Attribut</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.attributValuesList.valuesList">Values List</Translate>
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {attributValuesListList.map((attributValuesList, i) => (
                  <tr key={`entity-${i}`}>
                    <td>
                      <Button tag={Link} to={`${match.url}/${attributValuesList.id}`} color="link" size="sm">
                        {attributValuesList.id}
                      </Button>
                    </td>
                    <td>
                      {attributValuesList.attribut ? (
                        <Link to={`attribut/${attributValuesList.attribut.id}`}>{attributValuesList.attribut.id}</Link>
                      ) : (
                        ''
                      )}
                    </td>
                    <td>
                      {attributValuesList.valuesList ? (
                        <Link to={`values-list/${attributValuesList.valuesList.id}`}>{attributValuesList.valuesList.id}</Link>
                      ) : (
                        ''
                      )}
                    </td>
                    <td className="text-right">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`${match.url}/${attributValuesList.id}`} color="info" size="sm">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${attributValuesList.id}/edit`} color="primary" size="sm">
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${attributValuesList.id}/delete`} color="danger" size="sm">
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
              <Translate contentKey="pimnowApp.attributValuesList.home.notFound">No Attribut Values Lists found</Translate>
            </div>
          )}
        </div>
      </div>
    );
  }
}

const mapStateToProps = ({ attributValuesList }: IRootState) => ({
  attributValuesListList: attributValuesList.entities
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
)(AttributValuesList);
