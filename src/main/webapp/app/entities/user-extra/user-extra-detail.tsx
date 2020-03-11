import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './user-extra.reducer';

export interface IUserExtraDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class UserExtraDetail extends React.Component<IUserExtraDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { userExtraEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.userExtra.detail.title">UserExtra</Translate> [<b>{userExtraEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="phone">
                <Translate contentKey="pimnowApp.userExtra.phone">Phone</Translate>
              </span>
            </dt>
            <dd>{userExtraEntity.phone}</dd>
            <dt>
              <Translate contentKey="pimnowApp.userExtra.customer">Customer</Translate>
            </dt>
            <dd>{userExtraEntity.customer ? userExtraEntity.customer.id : ''}</dd>
            <dt>
              <Translate contentKey="pimnowApp.userExtra.user">User</Translate>
            </dt>
            <dd>{userExtraEntity.user ? userExtraEntity.user.login : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/user-extra" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/user-extra/${userExtraEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ userExtra }: IRootState) => ({
  userExtraEntity: userExtra.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(UserExtraDetail);
