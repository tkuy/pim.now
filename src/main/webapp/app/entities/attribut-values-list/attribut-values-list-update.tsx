import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Label, Row} from 'reactstrap';
import {AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {IRootState} from 'app/shared/reducers';
import {getEntities as getAttributs} from 'app/entities/attribut/attribut.reducer';
import {getEntities as getValuesLists} from 'app/entities/values-list/values-list.reducer';
import {createEntity, getEntity, reset, updateEntity} from './attribut-values-list.reducer';

export interface IAttributValuesListUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IAttributValuesListUpdateState {
  isNew: boolean;
  attributId: string;
  valuesListId: string;
}

export class AttributValuesListUpdate extends React.Component<IAttributValuesListUpdateProps, IAttributValuesListUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      attributId: '0',
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

    this.props.getAttributs();
    this.props.getValuesLists();
  }

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { attributValuesListEntity } = this.props;
      const entity = {
        ...attributValuesListEntity,
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
    this.props.history.push('/entity/attribut-values-list');
  };

  render() {
    const { attributValuesListEntity, attributs, valuesLists, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="pimnowApp.attributValuesList.home.createOrEditLabel">
              <Translate contentKey="pimnowApp.attributValuesList.home.createOrEditLabel">Create or edit a AttributValuesList</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : attributValuesListEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="attribut-values-list-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="attribut-values-list-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label for="attribut-values-list-attribut">
                    <Translate contentKey="pimnowApp.attributValuesList.attribut">Attribut</Translate>
                  </Label>
                  <AvInput id="attribut-values-list-attribut" type="select" className="form-control" name="attribut.id">
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
                  <Label for="attribut-values-list-valuesList">
                    <Translate contentKey="pimnowApp.attributValuesList.valuesList">Values List</Translate>
                  </Label>
                  <AvInput id="attribut-values-list-valuesList" type="select" className="form-control" name="valuesList.id">
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
                <Button tag={Link} id="cancel-save" to="/entity/attribut-values-list" replace color="info">
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
  valuesLists: storeState.valuesList.entities,
  attributValuesListEntity: storeState.attributValuesList.entity,
  loading: storeState.attributValuesList.loading,
  updating: storeState.attributValuesList.updating,
  updateSuccess: storeState.attributValuesList.updateSuccess
});

const mapDispatchToProps = {
  getAttributs,
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
)(AttributValuesListUpdate);
