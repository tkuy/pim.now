import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './attribut.reducer';

export interface IAttributDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class AttributDetail extends React.Component<IAttributDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { attributEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.attribut.detail.title">Attribut</Translate> [<b>{attributEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="idF">
                <Translate contentKey="pimnowApp.attribut.idF">Id F</Translate>
              </span>
            </dt>
            <dd>{attributEntity.idF}</dd>
            <dt>
              <span id="nom">
                <Translate contentKey="pimnowApp.attribut.nom">Nom</Translate>
              </span>
            </dt>
            <dd>{attributEntity.nom}</dd>
            <dt>
              <span id="type">
                <Translate contentKey="pimnowApp.attribut.type">Type</Translate>
              </span>
            </dt>
            <dd>{attributEntity.type}</dd>
            <dt>
              <Translate contentKey="pimnowApp.attribut.customer">Customer</Translate>
            </dt>
            <dd>{attributEntity.customer ? attributEntity.customer.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/attribut" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/attribut/${attributEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ attribut }: IRootState) => ({
  attributEntity: attribut.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AttributDetail);
