import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, InputGroup, Row, Table} from 'reactstrap';
import {AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntities, getSearchEntities} from './user-extra.reducer';

export interface IUserExtraProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export interface IUserExtraState {
  search: string;
}

export class UserExtra extends React.Component<IUserExtraProps, IUserExtraState> {
  state: IUserExtraState = {
    search: ''
  };

  componentDidMount() {
    this.props.getEntities();
  }


  render() {
    const { userExtraList, match } = this.props;
    return (
      <div>
        <h2 id="user-extra-heading">
          <Translate contentKey="pimnowApp.userExtra.home.title">User Extras</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="pimnowApp.userExtra.home.createLabel">Create a new User Extra</Translate>
          </Link>
        </h2>

        <div className="table-responsive">
          {userExtraList && userExtraList.length > 0 ? (
            <Table responsive aria-describedby="user-extra-heading">
              <thead>
                <tr>
                  <th>
                    <Translate contentKey="global.field.id">ID</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.userExtra.phone">Phone</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.userExtra.customer">Customer</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.userExtra.user">User</Translate>
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {userExtraList.map((userExtra, i) => (
                  <tr key={`entity-${i}`}>
                    <td>
                      <Button tag={Link} to={`${match.url}/${userExtra.id}`} color="link" size="sm">
                        {userExtra.id}
                      </Button>
                    </td>
                    <td>{userExtra.phone}</td>
                    <td>{userExtra.customer ? <Link to={`customer/${userExtra.customer.id}`}>{userExtra.customer.id}</Link> : ''}</td>
                    <td>{userExtra.user ? userExtra.user.login : ''}</td>
                    <td className="text-right">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`${match.url}/${userExtra.id}`} color="info" size="sm">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${userExtra.id}/edit`} color="primary" size="sm">
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${userExtra.id}/delete`} color="danger" size="sm">
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
              <Translate contentKey="pimnowApp.userExtra.home.notFound">No User Extras found</Translate>
            </div>
          )}
        </div>
      </div>
    );
  }
}

const mapStateToProps = ({ userExtra }: IRootState) => ({
  userExtraList: userExtra.entities
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
)(UserExtra);
