import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Customer from './customer';
import CustomerDetail from './customer-detail';
import CustomerUpdate from './customer-update';
import CustomerDeleteDialog from './customer-delete-dialog';
import PrivateRoute from "app/shared/auth/private-route";
import {AUTHORITIES} from "app/config/constants";

const Routes = ({ match }) => (
  <>
    <Switch>
      <PrivateRoute exact path={`${match.url}/new`} component={CustomerUpdate} hasAnyAuthorities={[AUTHORITIES.ADMIN]} />
      <PrivateRoute exact path={`${match.url}/:id/edit`} component={CustomerUpdate} hasAnyAuthorities={[AUTHORITIES.ADMIN]}/>
      <PrivateRoute exact path={`${match.url}/:id/detail`} component={CustomerUpdate} hasAnyAuthorities={[AUTHORITIES.ADMIN]}/>
      <PrivateRoute path={match.url} component={Customer} hasAnyAuthorities={[AUTHORITIES.ADMIN]} />
    </Switch>
    <PrivateRoute path={`${match.url}/:id/delete`} component={CustomerDeleteDialog} hasAnyAuthorities={[AUTHORITIES.ADMIN]}/>
  </>
);

export default Routes;
