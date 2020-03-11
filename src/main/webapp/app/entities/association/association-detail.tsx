import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './association.reducer';

export interface IAssociationDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class AssociationDetail extends React.Component<IAssociationDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { associationEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.association.detail.title">Association</Translate> [<b>{associationEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="column">
                <Translate contentKey="pimnowApp.association.column">Column</Translate>
              </span>
            </dt>
            <dd>{associationEntity.column}</dd>
            <dt>
              <span id="idFAttribut">
                <Translate contentKey="pimnowApp.association.idFAttribut">Id F Attribut</Translate>
              </span>
            </dt>
            <dd>{associationEntity.idFAttribut}</dd>
            <dt>
              <Translate contentKey="pimnowApp.association.mapping">Mapping</Translate>
            </dt>
            <dd>{associationEntity.mapping ? associationEntity.mapping.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/association" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/association/${associationEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ association }: IRootState) => ({
  associationEntity: association.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AssociationDetail);
