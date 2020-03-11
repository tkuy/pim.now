import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './customer.reducer';

export interface ICustomerDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class CustomerDetail extends React.Component<ICustomerDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { customerEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.customer.detail.title">Customer</Translate> [<b>{customerEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="idF">
                <Translate contentKey="pimnowApp.customer.idF">Id F</Translate>
              </span>
            </dt>
            <dd>{customerEntity.idF}</dd>
            <dt>
              <span id="name">
                <Translate contentKey="pimnowApp.customer.name">Name</Translate>
              </span>
            </dt>
            <dd>{customerEntity.name}</dd>
            <dt>
              <span id="description">
                <Translate contentKey="pimnowApp.customer.description">Description</Translate>
              </span>
            </dt>
            <dd>{customerEntity.description}</dd>
            <dt>
              <span id="familyRoot">
                <Translate contentKey="pimnowApp.customer.familyRoot">Family Root</Translate>
              </span>
            </dt>
            <dd>{customerEntity.familyRoot}</dd>
            <dt>
              <span id="categoryRoot">
                <Translate contentKey="pimnowApp.customer.categoryRoot">Category Root</Translate>
              </span>
            </dt>
            <dd>{customerEntity.categoryRoot}</dd>
            <dt>
              <Translate contentKey="pimnowApp.customer.configuration">Configuration</Translate>
            </dt>
            <dd>{customerEntity.configuration ? customerEntity.configuration.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/customer" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/customer/${customerEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ customer }: IRootState) => ({
  customerEntity: customer.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CustomerDetail);
