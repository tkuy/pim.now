import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, InputGroup, Row, Table} from 'reactstrap';
import {AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntities, getSearchEntities} from './attribut.reducer';

export interface IAttributProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export interface IAttributState {
  search: string;
}

export class Attribut extends React.Component<IAttributProps, IAttributState> {
  state: IAttributState = {
    search: ''
  };

  componentDidMount() {
    this.props.getEntities();
  }

   render() {
    const { attributList, match } = this.props;
    return (
      <div>
        <h2 id="attribut-heading">
          <Translate contentKey="pimnowApp.attribut.home.title">Attributs</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="pimnowApp.attribut.home.createLabel">Create a new Attribut</Translate>
          </Link>
        </h2>
        <br/>
        <div className="table-responsive">
          {attributList && attributList.length > 0 ? (
            <Table responsive aria-describedby="attribut-heading">
              <thead>
                <tr>
                  <th>
                    <Translate contentKey="global.field.id">ID</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.attribut.idF">Id F</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.attribut.nom">Nom</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.attribut.type">Type</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.attribut.customer">Customer</Translate>
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {attributList.map((attribut, i) => (
                  <tr key={`entity-${i}`}>
                    <td>
                      <Button tag={Link} to={`${match.url}/${attribut.id}`} color="link" size="sm">
                        {attribut.id}
                      </Button>
                    </td>
                    <td>{attribut.idF}</td>
                    <td>{attribut.nom}</td>
                    <td>
                      <Translate contentKey={`pimnowApp.AttributType.${attribut.type}`} />
                    </td>
                    <td>{attribut.customer ? <Link to={`customer/${attribut.customer.id}`}>{attribut.customer.id}</Link> : ''}</td>
                    <td className="text-right">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`${match.url}/${attribut.id}`} color="info" size="sm">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${attribut.id}/edit`} color="primary" size="sm">
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${attribut.id}/delete`} color="danger" size="sm">
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
              <Translate contentKey="pimnowApp.attribut.home.notFound">No Attributs found</Translate>
            </div>
          )}
        </div>
      </div>
    );
  }
}

const mapStateToProps = ({ attribut }: IRootState) => ({
  attributList: attribut.entities
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
)(Attribut);
