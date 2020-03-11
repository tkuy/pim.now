import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Label, Row} from 'reactstrap';
import {AvField, AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {IRootState} from 'app/shared/reducers';
import {getEntities as getProducts} from 'app/entities/product/product.reducer';
import {createEntity, getEntity, reset, updateEntity} from './prestashop-product.reducer';

export interface IPrestashopProductUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IPrestashopProductUpdateState {
  isNew: boolean;
  productPimId: string;
}

export class PrestashopProductUpdate extends React.Component<IPrestashopProductUpdateProps, IPrestashopProductUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      productPimId: '0',
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

    this.props.getProducts();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { prestashopProductEntity } = this.props;
      const entity = {
        ...prestashopProductEntity,
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
    this.props.history.push('/entity/prestashop-product');
  };

  render() {
    const { prestashopProductEntity, products, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="pimnowApp.prestashopProduct.home.createOrEditLabel">
              <Translate contentKey="pimnowApp.prestashopProduct.home.createOrEditLabel">Create or edit a PrestashopProduct</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : prestashopProductEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="prestashop-product-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="prestashop-product-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="prestashopProductIdLabel" for="prestashop-product-prestashopProductId">
                    <Translate contentKey="pimnowApp.prestashopProduct.prestashopProductId">Prestashop Product Id</Translate>
                  </Label>
                  <AvField
                    id="prestashop-product-prestashopProductId"
                    type="string"
                    className="form-control"
                    name="prestashopProductId"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      number: { value: true, errorMessage: translate('entity.validation.number') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label for="prestashop-product-productPim">
                    <Translate contentKey="pimnowApp.prestashopProduct.productPim">Product Pim</Translate>
                  </Label>
                  <AvInput id="prestashop-product-productPim" type="select" className="form-control" name="productPim.id">
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
                <Button tag={Link} id="cancel-save" to="/entity/prestashop-product" replace color="info">
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
  products: storeState.product.entities,
  prestashopProductEntity: storeState.prestashopProduct.entity,
  loading: storeState.prestashopProduct.loading,
  updating: storeState.prestashopProduct.updating,
  updateSuccess: storeState.prestashopProduct.updateSuccess
});

const mapDispatchToProps = {
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
)(PrestashopProductUpdate);
