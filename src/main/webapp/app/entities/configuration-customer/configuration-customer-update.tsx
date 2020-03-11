import React, {useEffect} from 'react';

import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntities as getCustomers } from 'app/entities/customer/customer.reducer';
import { getEntity, updateEntity, createEntity, reset } from './configuration-customer.reducer';

import {IMappingUpdateState} from "app/entities/mapping/mapping-update";


export interface IConfigurationCustomerUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IConfigurationCustomerUpdateState {
  isNew: boolean;
  customerId: string;
}

export class ConfigurationCustomerUpdate extends React.Component<IConfigurationCustomerUpdateProps, IConfigurationCustomerUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      customerId: '0',
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    if (this.state.isNew) {
      this.props.reset();
    } else {
      this.props.getEntity(this.props.match.params.id);
    }

    this.props.getCustomers();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { configurationCustomerEntity } = this.props;
      const entity = {
        ...configurationCustomerEntity,
        ...values
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/configuration-customer');
  };

  render() {
    const { configurationCustomerEntity, customers, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="pimnowApp.configurationCustomer.home.createOrEditLabel">
              <Translate contentKey="pimnowApp.configurationCustomer.home.createOrEditLabel">
                Create or edit a ConfigurationCustomer
              </Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : configurationCustomerEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="configuration-customer-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="configuration-customer-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="urlPrestashopLabel" for="configuration-customer-urlPrestashop">
                    <Translate contentKey="pimnowApp.configurationCustomer.urlPrestashop">Url Prestashop</Translate>
                  </Label>
                  <AvField
                    id="configuration-customer-urlPrestashop"
                    type="text"
                    name="urlPrestashop"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="apiKeyPrestashopLabel" for="configuration-customer-apiKeyPrestashop">
                    <Translate contentKey="pimnowApp.configurationCustomer.apiKeyPrestashop">Api Key Prestashop</Translate>
                  </Label>
                  <AvField
                    id="configuration-customer-apiKeyPrestashop"
                    type="text"
                    name="apiKeyPrestashop"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label for="configuration-customer-customer">
                    <Translate contentKey="pimnowApp.configurationCustomer.customer">Customer</Translate>
                  </Label>
                  <AvInput id="configuration-customer-customer" type="select" className="form-control" name="customer.id">
                    <option value="" key="0" />
                    {customers
                      ? customers.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/configuration-customer" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" />
                  &nbsp;
                  <span className="d-none d-md-inline">
                    <Translate contentKey="entity.action.back">Back</Translate>
                  </span>
                </Button>
                &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                  <FontAwesomeIcon icon="save" />
                  &nbsp;
                  <Translate contentKey="entity.action.save">Save</Translate>
                </Button>
              </AvForm>
            )}
          </Col>
        </Row>
      </div>
    );
  }
}

const MappingUpdate = (props: IMappingUpdateState) => {
  const [customerId, setCustomerId] = React.useState('0') ;

  useEffect(() => {
    props.getEntity(props.match.params.id);
    props.getCustomers();
  }, []);

}

const mapStateToProps = (storeState: IRootState) => ({
  customers: storeState.customer.entities,
  configurationCustomerEntity: storeState.configurationCustomer.entity,
  loading: storeState.configurationCustomer.loading,
  updating: storeState.configurationCustomer.updating,
  updateSuccess: storeState.configurationCustomer.updateSuccess
});

const mapDispatchToProps = {
  getCustomers,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ConfigurationCustomerUpdate);
