import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './category.reducer';

export interface ICategoryDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class CategoryDetail extends React.Component<ICategoryDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { categoryEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.category.detail.title">Category</Translate> [<b>{categoryEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="idF">
                <Translate contentKey="pimnowApp.category.idF">Id F</Translate>
              </span>
            </dt>
            <dd>{categoryEntity.idF}</dd>
            <dt>
              <span id="nom">
                <Translate contentKey="pimnowApp.category.nom">Nom</Translate>
              </span>
            </dt>
            <dd>{categoryEntity.nom}</dd>
            <dt>
              <Translate contentKey="pimnowApp.category.predecessor">Predecessor</Translate>
            </dt>
            <dd>{categoryEntity.predecessor ? categoryEntity.predecessor.id : ''}</dd>
            <dt>
              <Translate contentKey="pimnowApp.category.customer">Customer</Translate>
            </dt>
            <dd>{categoryEntity.customer ? categoryEntity.customer.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/category" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/category/${categoryEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ category }: IRootState) => ({
  categoryEntity: category.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CategoryDetail);
