import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import PrestashopProduct from './prestashop-product';
import PrestashopProductDetail from './prestashop-product-detail';
import PrestashopProductUpdate from './prestashop-product-update';
import PrestashopProductDeleteDialog from './prestashop-product-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={PrestashopProductUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={PrestashopProductUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={PrestashopProductDetail} />
      <ErrorBoundaryRoute path={match.url} component={PrestashopProduct} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={PrestashopProductDeleteDialog} />
  </>
);

export default Routes;
