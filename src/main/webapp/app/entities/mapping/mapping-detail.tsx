import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Row} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './mapping.reducer';

export interface IMappingDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class MappingDetail extends React.Component<IMappingDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { mappingEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="pimnowApp.mapping.detail.title">Mapping</Translate> [<b>{mappingEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="idF">
                <Translate contentKey="pimnowApp.mapping.idF">Id F</Translate>
              </span>
            </dt>
            <dd>{mappingEntity.idF}</dd>
            <dt>
              <span id="name">
                <Translate contentKey="pimnowApp.mapping.name">Name</Translate>
              </span>
            </dt>
            <dd>{mappingEntity.name}</dd>
            <dt>
              <span id="description">
                <Translate contentKey="pimnowApp.mapping.description">Description</Translate>
              </span>
            </dt>
            <dd>{mappingEntity.description}</dd>
            <dt>
              <span id="separator">
                <Translate contentKey="pimnowApp.mapping.separator">Separator</Translate>
              </span>
            </dt>
            <dd>{mappingEntity.separator}</dd>
            <dt>
              <Translate contentKey="pimnowApp.mapping.customer">Customer</Translate>
            </dt>
            <dd>{mappingEntity.customer ? mappingEntity.customer.id : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/mapping" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/mapping/${mappingEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ mapping }: IRootState) => ({
  mappingEntity: mapping.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MappingDetail);
