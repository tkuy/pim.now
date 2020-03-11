import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './family.reducer';

export interface IFamilyDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class FamilyDetail extends React.Component<IFamilyDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { familyEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.family.detail.title">Family</Translate> [<b>{familyEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="idF">
                <Translate contentKey="pimnowApp.family.idF">Id F</Translate>
              </span>
            </dt>
            <dd>{familyEntity.idF}</dd>
            <dt>
              <span id="nom">
                <Translate contentKey="pimnowApp.family.nom">Nom</Translate>
              </span>
            </dt>
            <dd>{familyEntity.nom}</dd>
            <dt>
              <Translate contentKey="pimnowApp.family.family">Family</Translate>
            </dt>
            <dd>{familyEntity.family ? familyEntity.family.id : ''}</dd>
            <dt>
              <Translate contentKey="pimnowApp.family.predecessor">Predecessor</Translate>
            </dt>
            <dd>{familyEntity.predecessor ? familyEntity.predecessor.id : ''}</dd>
            <dt>
              <Translate contentKey="pimnowApp.family.customer">Customer</Translate>
            </dt>
            <dd>{familyEntity.customer ? familyEntity.customer.id : ''}</dd>
            <dt>
              <Translate contentKey="pimnowApp.family.attribute">Attribute</Translate>
            </dt>
            <dd>
              {familyEntity.attributes
                ? familyEntity.attributes.map((val, i) => (
                    <span key={val.id}>
                      <a>{val.id}</a>
                      {i === familyEntity.attributes.length - 1 ? '' : ', '}
                    </span>
                  ))
                : null}
            </dd>
          </dl>
          <Button tag={Link} to="/entity/family" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/family/${familyEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ family }: IRootState) => ({
  familyEntity: family.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(FamilyDetail);
