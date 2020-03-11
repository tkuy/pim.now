import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, InputGroup, Row, Table} from 'reactstrap';
import {AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntities, getSearchEntities} from './prestashop-product.reducer';

export interface IPrestashopProductProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export interface IPrestashopProductState {
  search: string;
}

export class PrestashopProduct extends React.Component<IPrestashopProductProps, IPrestashopProductState> {
  state: IPrestashopProductState = {
    search: ''
  };

  componentDidMount() {
    this.props.getEntities();
  }

  render() {
    const { prestashopProductList, match } = this.props;
    return (
      <div>
        <h2 id="prestashop-product-heading">
          <Translate contentKey="pimnowApp.prestashopProduct.home.title">Prestashop Products</Translate>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="pimnowApp.prestashopProduct.home.createLabel">Create a new Prestashop Product</Translate>
          </Link>
        </h2>
        <br/>
        <div className="table-responsive">
          {prestashopProductList && prestashopProductList.length > 0 ? (
            <Table responsive aria-describedby="prestashop-product-heading">
              <thead>
                <tr>
                  <th>
                    <Translate contentKey="global.field.id">ID</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.prestashopProduct.prestashopProductId">Prestashop Product Id</Translate>
                  </th>
                  <th>
                    <Translate contentKey="pimnowApp.prestashopProduct.productPim">Product Pim</Translate>
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {prestashopProductList.map((prestashopProduct, i) => (
                  <tr key={`entity-${i}`}>
                    <td>
                      <Button tag={Link} to={`${match.url}/${prestashopProduct.id}`} color="link" size="sm">
                        {prestashopProduct.id}
                      </Button>
                    </td>
                    <td>{prestashopProduct.prestashopProductId}</td>
                    <td>
                      {prestashopProduct.productPim ? (
                        <Link to={`product/${prestashopProduct.productPim.id}`}>{prestashopProduct.productPim.id}</Link>
                      ) : (
                        ''
                      )}
                    </td>
                    <td className="text-right">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`${match.url}/${prestashopProduct.id}`} color="info" size="sm">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${prestashopProduct.id}/edit`} color="primary" size="sm">
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${prestashopProduct.id}/delete`} color="danger" size="sm">
                          <FontAwesomeIcon icon="trash" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.delete">Delete</Translate>
                          </span>
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            <div className="alert alert-warning">
              <Translate contentKey="pimnowApp.prestashopProduct.home.notFound">No Prestashop Products found</Translate>
            </div>
          )}
        </div>
      </div>
    );
  }
}

const mapStateToProps = ({ prestashopProduct }: IRootState) => ({
  prestashopProductList: prestashopProduct.entities
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(PrestashopProduct);
