import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Label, Row} from 'reactstrap';
import {AvField, AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {IRootState} from 'app/shared/reducers';
import {getEntities as getMappings} from 'app/entities/mapping/mapping.reducer';
import {createEntity, getEntity, reset, updateEntity} from './association.reducer';

export interface IAssociationUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IAssociationUpdateState {
  isNew: boolean;
  mappingId: string;
}

export class AssociationUpdate extends React.Component<IAssociationUpdateProps, IAssociationUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      mappingId: '0',
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

    this.props.getMappings();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { associationEntity } = this.props;
      const entity = {
        ...associationEntity,
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
    this.props.history.push('/entity/association');
  };

  render() {
    const { associationEntity, mappings, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="pimnowApp.association.home.createOrEditLabel">
              <Translate contentKey="pimnowApp.association.home.createOrEditLabel">Create or edit a Association</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : associationEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="association-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="association-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="columnLabel" for="association-column">
                    <Translate contentKey="pimnowApp.association.column">Column</Translate>
                  </Label>
                  <AvField
                    id="association-column"
                    type="text"
                    name="column"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="idFAttributLabel" for="association-idFAttribut">
                    <Translate contentKey="pimnowApp.association.idFAttribut">Id F Attribut</Translate>
                  </Label>
                  <AvField
                    id="association-idFAttribut"
                    type="text"
                    name="idFAttribut"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label for="association-mapping">
                    <Translate contentKey="pimnowApp.association.mapping">Mapping</Translate>
                  </Label>
                  <AvInput id="association-mapping" type="select" className="form-control" name="mapping.id">
                    <option value="" key="0" />
                    {mappings
                      ? mappings.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/association" replace color="info">
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
  mappings: storeState.mapping.entities,
  associationEntity: storeState.association.entity,
  loading: storeState.association.loading,
  updating: storeState.association.updating,
  updateSuccess: storeState.association.updateSuccess
});

const mapDispatchToProps = {
  getMappings,
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
)(AssociationUpdate);
