import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Label, Row} from 'reactstrap';
import {AvField, AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {IRootState} from 'app/shared/reducers';
import {getEntities as getProducts} from 'app/entities/product/product.reducer';
import {createEntity, getEntity, reset, updateEntity} from './workflow-state.reducer';

export interface IWorkflowStateUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IWorkflowStateUpdateState {
  isNew: boolean;
  productId: string;
}

export class WorkflowStateUpdate extends React.Component<IWorkflowStateUpdateProps, IWorkflowStateUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
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

    this.props.getProducts();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { workflowStateEntity } = this.props;
      const entity = {
        ...workflowStateEntity,
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
    this.props.history.push('/entity/workflow-state');
  };

  render() {
    const { workflowStateEntity, products, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="pimnowApp.workflowState.home.createOrEditLabel">
              <Translate contentKey="pimnowApp.workflowState.home.createOrEditLabel">Create or edit a WorkflowState</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : workflowStateEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="workflow-state-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="workflow-state-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="stateLabel" for="workflow-state-state">
                    <Translate contentKey="pimnowApp.workflowState.state">State</Translate>
                  </Label>
                  <AvInput
                    id="workflow-state-state"
                    type="select"
                    className="form-control"
                    name="state"
                    value={(!isNew && workflowStateEntity.state) || 'IN_PROGRESS'}
                  >
                    <option value="IN_PROGRESS">{translate('pimnowApp.EnumWorkflowState.IN_PROGRESS')}</option>
                    <option value="REFUSED">{translate('pimnowApp.EnumWorkflowState.REFUSED')}</option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label id="failDescriptionLabel" for="workflow-state-failDescription">
                    <Translate contentKey="pimnowApp.workflowState.failDescription">Fail Description</Translate>
                  </Label>
                  <AvField id="workflow-state-failDescription" type="text" name="failDescription" />
                </AvGroup>
                <AvGroup>
                  <Label for="workflow-state-product">
                    <Translate contentKey="pimnowApp.workflowState.product">Product</Translate>
                  </Label>
                  <AvInput id="workflow-state-product" type="select" className="form-control" name="product.id">
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
                <Button tag={Link} id="cancel-save" to="/entity/workflow-state" replace color="info">
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
  workflowStateEntity: storeState.workflowState.entity,
  loading: storeState.workflowState.loading,
  updating: storeState.workflowState.updating,
  updateSuccess: storeState.workflowState.updateSuccess
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
)(WorkflowStateUpdate);
