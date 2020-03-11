import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './attribut-value.reducer';

export interface IAttributValueDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class AttributValueDetail extends React.Component<IAttributValueDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { attributValueEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.attributValue.detail.title">AttributValue</Translate> [<b>{attributValueEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="value">
                <Translate contentKey="pimnowApp.attributValue.value">Value</Translate>
              </span>
            </dt>
            <dd>{attributValueEntity.value}</dd>
            <dt>
              <Translate contentKey="pimnowApp.attributValue.attribut">Attribut</Translate>
            </dt>
            <dd>{attributValueEntity.attribut ? attributValueEntity.attribut.id : ''}</dd>
            <dt>
              <Translate contentKey="pimnowApp.attributValue.product">Product</Translate>
            </dt>
            <dd>{attributValueEntity.product ? attributValueEntity.product.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/attribut-value" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/attribut-value/${attributValueEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ attributValue }: IRootState) => ({
  attributValueEntity: attributValue.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AttributValueDetail);
