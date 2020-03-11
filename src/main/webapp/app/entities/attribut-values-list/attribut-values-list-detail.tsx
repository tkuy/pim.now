import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './attribut-values-list.reducer';

export interface IAttributValuesListDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class AttributValuesListDetail extends React.Component<IAttributValuesListDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { attributValuesListEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.attributValuesList.detail.title">AttributValuesList</Translate> [
            <b>{attributValuesListEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <Translate contentKey="pimnowApp.attributValuesList.attribut">Attribut</Translate>
            </dt>
            <dd>{attributValuesListEntity.attribut ? attributValuesListEntity.attribut.id : ''}</dd>
            <dt>
              <Translate contentKey="pimnowApp.attributValuesList.valuesList">Values List</Translate>
            </dt>
            <dd>{attributValuesListEntity.valuesList ? attributValuesListEntity.valuesList.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/attribut-values-list" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/attribut-values-list/${attributValuesListEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ attributValuesList }: IRootState) => ({
  attributValuesListEntity: attributValuesList.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(AttributValuesListDetail);
