import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './values-list-item.reducer';

export interface IValuesListItemDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class ValuesListItemDetail extends React.Component<IValuesListItemDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { valuesListItemEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.valuesListItem.detail.title">ValuesListItem</Translate> [<b>{valuesListItemEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="value">
                <Translate contentKey="pimnowApp.valuesListItem.value">Value</Translate>
              </span>
            </dt>
            <dd>{valuesListItemEntity.value}</dd>
            <dt>
              <Translate contentKey="pimnowApp.valuesListItem.valuesList">Values List</Translate>
            </dt>
            <dd>{valuesListItemEntity.valuesList ? valuesListItemEntity.valuesList.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/values-list-item" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/values-list-item/${valuesListItemEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ valuesListItem }: IRootState) => ({
  valuesListItemEntity: valuesListItem.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ValuesListItemDetail);
