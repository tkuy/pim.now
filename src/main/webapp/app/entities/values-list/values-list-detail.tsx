import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './values-list.reducer';

export interface IValuesListDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ValuesListDetail extends React.Component<IValuesListDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { valuesListEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.valuesList.detail.title">ValuesList</Translate> [<b>{valuesListEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="idF">
                <Translate contentKey="pimnowApp.valuesList.idF">Id F</Translate>
              </span>
            </dt>
            <dd>{valuesListEntity.idF}</dd>
            <dt>
              <Translate contentKey="pimnowApp.valuesList.customer">Customer</Translate>
            </dt>
            <dd>{valuesListEntity.customer ? valuesListEntity.customer.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/values-list" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/values-list/${valuesListEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ valuesList }: IRootState) => ({
  valuesListEntity: valuesList.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ValuesListDetail);
