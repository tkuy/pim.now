import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, InputGroup, Row, Table} from 'reactstrap';
import {AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntities, getSearchEntities} from './association.reducer';

export interface IAssociationProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export interface IAssociationState {
  search: string;
}

export class Association extends React.Component<IAssociationProps, IAssociationState> {
  state: IAssociationState = {
    search: ''
  };

  componentDidMount() {
    this.props.getEntities();
  }

  render() {
    const { associationList, match } = this.props;
    return (
      <div>
        <h2 id="association-heading">
          <Translate contentKey="pimnowApp.association.home.title">Associations</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="pimnowApp.association.home.createLabel">Create a new Association</Translate>
          </Link>
        </h2>
        <br/>
        <div className="table-responsive">
          {associationList && associationList.length > 0 ? (
            <Table responsive aria-describedby="association-heading">
              <thead>
                <tr>
                  <th>
                    <Translate contentKey="global.field.id">ID</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.association.column">Column</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.association.idFAttribut">Id F Attribut</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.association.mapping">Mapping</Translate>
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {associationList.map((association, i) => (
                  <tr key={`entity-${i}`}>
                    <td>
                      <Button tag={Link} to={`${match.url}/${association.id}`} color="link" size="sm">
                        {association.id}
                      </Button>
                    </td>
                    <td>{association.column}</td>
                    <td>{association.idFAttribut}</td>
                    <td>{association.mapping ? <Link to={`mapping/${association.mapping.id}`}>{association.mapping.id}</Link> : ''}</td>
                    <td className="text-right">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`${match.url}/${association.id}`} color="info" size="sm">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${association.id}/edit`} color="primary" size="sm">
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${association.id}/delete`} color="danger" size="sm">
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
              <Translate contentKey="pimnowApp.association.home.notFound">No Associations found</Translate>
            </div>
          )}
        </div>
      </div>
    );
  }
}

const mapStateToProps = ({ association }: IRootState) => ({
  associationList: association.entities
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
)(Association);
