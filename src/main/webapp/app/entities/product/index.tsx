import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Product from './product';
import ProductDetail from './product-detail';
import ProductUpdate from './product-update';
import ProductDeleteDialog from './product-delete-dialog';
import PrivateRoute from "app/shared/auth/private-route";
import {AUTHORITIES} from "app/config/constants";

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ProductUpdate}/>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ProductUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ProductDetail} />
      <ErrorBoundaryRoute path={match.url} component={Product} />
    </Switch>
    <PrivateRoute path={`${match.url}/:id/delete`} component={ProductDeleteDialog} hasAnyAuthorities={[AUTHORITIES.ADMIN_FONC]}/>
  </>
);

export default Routes;
