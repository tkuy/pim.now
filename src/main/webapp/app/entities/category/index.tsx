import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Category from './category';
import CategoryUpdate from './category-update';
import CategoryDeleteDialog from './category-delete-dialog';
import {AUTHORITIES} from "app/config/constants";
import PrivateRoute from "app/shared/auth/private-route";

const Routes = ({ match }) => (
  <>
    <Switch>
      <PrivateRoute exact path={`${match.url}/new`} component={CategoryUpdate} hasAnyAuthorities={[AUTHORITIES.ADMIN_FONC]}/>
      <PrivateRoute exact path={`${match.url}/:id/edit`} component={CategoryUpdate} hasAnyAuthorities={[AUTHORITIES.ADMIN_FONC]}/>
      <ErrorBoundaryRoute path={match.url} component={Category} />
    </Switch>
    <PrivateRoute path={`${match.url}/:id/delete`} component={CategoryDeleteDialog} hasAnyAuthorities={[AUTHORITIES.ADMIN_FONC]}/>
  </>
);

export default Routes;
