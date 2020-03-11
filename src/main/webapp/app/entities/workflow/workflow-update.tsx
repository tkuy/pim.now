import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Label, Row} from 'reactstrap';
import {AvField, AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {IRootState} from 'app/shared/reducers';
import {getEntities as getCustomers} from 'app/entities/customer/customer.reducer';
import {createEntity, getEntity, reset, updateEntity} from './workflow.reducer';

export interface IWorkflowUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IWorkflowUpdateState {
  isNew: boolean;
  customerId: string;
}

export class WorkflowUpdate extends React.Component<IWorkflowUpdateProps, IWorkflowUpdateState> {
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
      const { workflowEntity } = this.props;
      const entity = {
        ...workflowEntity,
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
    this.props.history.push('/entity/workflow');
  };

  render() {
    const { workflowEntity, customers, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="pimnowApp.workflow.home.createOrEditLabel">
              <Translate contentKey="pimnowApp.workflow.home.createOrEditLabel">Create or edit a Workflow</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : workflowEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="workflow-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="workflow-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="idFLabel" for="workflow-idF">
                    <Translate contentKey="pimnowApp.workflow.idF">Id F</Translate>
                  </Label>
                  <AvField
                    id="workflow-idF"
                    type="text"
                    name="idF"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="nameLabel" for="workflow-name">
                    <Translate contentKey="pimnowApp.workflow.name">Name</Translate>
                  </Label>
                  <AvField
                    id="workflow-name"
                    type="text"
                    name="name"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="descriptionLabel" for="workflow-description">
                    <Translate contentKey="pimnowApp.workflow.description">Description</Translate>
                  </Label>
                  <AvField id="workflow-description" type="text" name="description" />
                </AvGroup>
                <AvGroup>
                  <Label for="workflow-customer">
                    <Translate contentKey="pimnowApp.workflow.customer">Customer</Translate>
                  </Label>
                  <AvInput id="workflow-customer" type="select" className="form-control" name="customer.id">
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
                <Button tag={Link} id="cancel-save" to="/entity/workflow" replace color="info">
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
  workflowEntity: storeState.workflow.entity,
  loading: storeState.workflow.loading,
  updating: storeState.workflow.updating,
  updateSuccess: storeState.workflow.updateSuccess
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
)(WorkflowUpdate);
