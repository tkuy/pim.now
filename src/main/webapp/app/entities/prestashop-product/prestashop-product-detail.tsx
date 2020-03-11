import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './prestashop-product.reducer';

export interface IPrestashopProductDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class PrestashopProductDetail extends React.Component<IPrestashopProductDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { prestashopProductEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.prestashopProduct.detail.title">PrestashopProduct</Translate> [
            <b>{prestashopProductEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="prestashopProductId">
                <Translate contentKey="pimnowApp.prestashopProduct.prestashopProductId">Prestashop Product Id</Translate>
              </span>
            </dt>
            <dd>{prestashopProductEntity.prestashopProductId}</dd>
            <dt>
              <Translate contentKey="pimnowApp.prestashopProduct.productPim">Product Pim</Translate>
            </dt>
            <dd>{prestashopProductEntity.productPim ? prestashopProductEntity.productPim.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/prestashop-product" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/prestashop-product/${prestashopProductEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ prestashopProduct }: IRootState) => ({
  prestashopProductEntity: prestashopProduct.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(PrestashopProductDetail);
