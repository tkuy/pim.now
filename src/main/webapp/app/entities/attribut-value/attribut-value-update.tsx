import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Label, Row} from 'reactstrap';
import {AvField, AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {IRootState} from 'app/shared/reducers';
import {getEntities as getAttributs} from 'app/entities/attribut/attribut.reducer';
import {getEntities as getProducts} from 'app/entities/product/product.reducer';
import {createEntity, getEntity, reset, updateEntity} from './attribut-value.reducer';

export interface IAttributValueUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IAttributValueUpdateState {
  isNew: boolean;
  attributId: string;
  productId: string;
}

export class AttributValueUpdate extends React.Component<IAttributValueUpdateProps, IAttributValueUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      attributId: '0',
      productId: '0',
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

    this.props.getAttributs();
    this.props.getProducts();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { attributValueEntity } = this.props;
      const entity = {
        ...attributValueEntity,
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
    this.props.history.push('/entity/attribut-value');
  };

  render() {
    const { attributValueEntity, attributs, products, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="pimnowApp.attributValue.home.createOrEditLabel">
              <Translate contentKey="pimnowApp.attributValue.home.createOrEditLabel">Create or edit a AttributValue</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : attributValueEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="attribut-value-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="attribut-value-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="valueLabel" for="attribut-value-value">
                    <Translate contentKey="pimnowApp.attributValue.value">Value</Translate>
                  </Label>
                  <AvField id="attribut-value-value" type="text" name="value" />
                </AvGroup>
                <AvGroup>
                  <Label for="attribut-value-attribut">
                    <Translate contentKey="pimnowApp.attributValue.attribut">Attribut</Translate>
                  </Label>
                  <AvInput id="attribut-value-attribut" type="select" className="form-control" name="attribut.id">
                    <option value="" key="0" />
                    {attributs
                      ? attributs.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="attribut-value-product">
                    <Translate contentKey="pimnowApp.attributValue.product">Product</Translate>
                  </Label>
                  <AvInput id="attribut-value-product" type="select" className="form-control" name="product.id">
                    <option value="" key="0" />
                    {products
                      ? products.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/attribut-value" replace color="info">
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
  attributs: storeState.attribut.entities,
  products: storeState.product.entities,
  attributValueEntity: storeState.attributValue.entity,
  loading: storeState.attributValue.loading,
  updating: storeState.attributValue.updating,
  updateSuccess: storeState.attributValue.updateSuccess
});

const mapDispatchToProps = {
  getAttributs,
  getProducts,
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
)(AttributValueUpdate);
