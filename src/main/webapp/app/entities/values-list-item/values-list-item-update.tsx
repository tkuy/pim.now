import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Label, Row} from 'reactstrap';
import {AvField, AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {IRootState} from 'app/shared/reducers';
import {getEntities as getValuesLists} from 'app/entities/values-list/values-list.reducer';
import {createEntity, getEntity, reset, updateEntity} from './values-list-item.reducer';

export interface IValuesListItemUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IValuesListItemUpdateState {
  isNew: boolean;
  valuesListId: string;
}

export class ValuesListItemUpdate extends React.Component<IValuesListItemUpdateProps, IValuesListItemUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      valuesListId: '0',
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

    this.props.getValuesLists();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { valuesListItemEntity } = this.props;
      const entity = {
        ...valuesListItemEntity,
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
    this.props.history.push('/entity/values-list-item');
  };

  render() {
    const { valuesListItemEntity, valuesLists, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="pimnowApp.valuesListItem.home.createOrEditLabel">
              <Translate contentKey="pimnowApp.valuesListItem.home.createOrEditLabel">Create or edit a ValuesListItem</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : valuesListItemEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="values-list-item-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="values-list-item-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="valueLabel" for="values-list-item-value">
                    <Translate contentKey="pimnowApp.valuesListItem.value">Value</Translate>
                  </Label>
                  <AvField
                    id="values-list-item-value"
                    type="text"
                    name="value"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label for="values-list-item-valuesList">
                    <Translate contentKey="pimnowApp.valuesListItem.valuesList">Values List</Translate>
                  </Label>
                  <AvInput id="values-list-item-valuesList" type="select" className="form-control" name="valuesList.id">
                    <option value="" key="0" />
                    {valuesLists
                      ? valuesLists.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/values-list-item" replace color="info">
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
  valuesLists: storeState.valuesList.entities,
  valuesListItemEntity: storeState.valuesListItem.entity,
  loading: storeState.valuesListItem.loading,
  updating: storeState.valuesListItem.updating,
  updateSuccess: storeState.valuesListItem.updateSuccess
});

const mapDispatchToProps = {
  getValuesLists,
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
)(ValuesListItemUpdate);
