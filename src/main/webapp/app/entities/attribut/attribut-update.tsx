import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Label, Row} from 'reactstrap';
import {AvField, AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {IRootState} from 'app/shared/reducers';
import {getEntities as getCustomers} from 'app/entities/customer/customer.reducer';
import {getEntities as getFamilies} from 'app/entities/family/family.reducer';
import {createEntity, getEntity, reset, updateEntity} from './attribut.reducer';

export interface IAttributUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IAttributUpdateState {
  isNew: boolean;
  customerId: string;
  familyId: string;
}

export class AttributUpdate extends React.Component<IAttributUpdateProps, IAttributUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      customerId: '0',
      familyId: '0',
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
    this.props.getFamilies();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { attributEntity } = this.props;
      const entity = {
        ...attributEntity,
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
    this.props.history.push('/entity/attribut');
  };

  render() {
    const { attributEntity, customers, families, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="pimnowApp.attribut.home.createOrEditLabel">
              <Translate contentKey="pimnowApp.attribut.home.createOrEditLabel">Create or edit a Attribut</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : attributEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="attribut-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="attribut-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="idFLabel" for="attribut-idF">
                    <Translate contentKey="pimnowApp.attribut.idF">Id F</Translate>
                  </Label>
                  <AvField
                    id="attribut-idF"
                    type="text"
                    name="idF"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="nomLabel" for="attribut-nom">
                    <Translate contentKey="pimnowApp.attribut.nom">Nom</Translate>
                  </Label>
                  <AvField
                    id="attribut-nom"
                    type="text"
                    name="nom"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="typeLabel" for="attribut-type">
                    <Translate contentKey="pimnowApp.attribut.type">Type</Translate>
                  </Label>
                  <AvInput
                    id="attribut-type"
                    type="select"
                    className="form-control"
                    name="type"
                    value={(!isNew && attributEntity.type) || 'TEXT'}
                  >
                    <option value="TEXT">{translate('pimnowApp.AttributType.TEXT')}</option>
                    <option value="RESSOURCE">{translate('pimnowApp.AttributType.RESSOURCE')}</option>
                    <option value="VALUES_LIST">{translate('pimnowApp.AttributType.VALUES_LIST')}</option>
                    <option value="MULTIPLE_VALUE">{translate('pimnowApp.AttributType.MULTIPLE_VALUE')}</option>
                    <option value="NUMBER">{translate('pimnowApp.AttributType.NUMBER')}</option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="attribut-customer">
                    <Translate contentKey="pimnowApp.attribut.customer">Customer</Translate>
                  </Label>
                  <AvInput id="attribut-customer" type="select" className="form-control" name="customer.id">
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
                <Button tag={Link} id="cancel-save" to="/entity/attribut" replace color="info">
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

const mapStateToProps = (storeState: IRootState) => ({
  customers: storeState.customer.entities,
  families: storeState.family.entities,
  attributEntity: storeState.attribut.entity,
  loading: storeState.attribut.loading,
  updating: storeState.attribut.updating,
  updateSuccess: storeState.attribut.updateSuccess
});

const mapDispatchToProps = {
  getCustomers,
  getFamilies,
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
)(AttributUpdate);
