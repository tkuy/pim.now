import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './configuration-customer.reducer';

export interface IConfigurationCustomerDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ConfigurationCustomerDetail extends React.Component<IConfigurationCustomerDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { configurationCustomerEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.configurationCustomer.detail.title">ConfigurationCustomer</Translate> [
            <b>{configurationCustomerEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="urlPrestashop">
                <Translate contentKey="pimnowApp.configurationCustomer.urlPrestashop">Url Prestashop</Translate>
              </span>
            </dt>
            <dd>{configurationCustomerEntity.urlPrestashop}</dd>
            <dt>
              <span id="apiKeyPrestashop">
                <Translate contentKey="pimnowApp.configurationCustomer.apiKeyPrestashop">Api Key Prestashop</Translate>
              </span>
            </dt>
            <dd>{configurationCustomerEntity.apiKeyPrestashop}</dd>
            <dt>
              <Translate contentKey="pimnowApp.configurationCustomer.customer">Customer</Translate>
            </dt>
            <dd>{configurationCustomerEntity.customer ? configurationCustomerEntity.customer.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/configuration-customer" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/configuration-customer/${configurationCustomerEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ configurationCustomer }: IRootState) => ({
  configurationCustomerEntity: configurationCustomer.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ConfigurationCustomerDetail);
