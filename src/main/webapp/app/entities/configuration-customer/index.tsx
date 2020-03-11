import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ConfigurationCustomer from './configuration-customer';
import PrivateRoute from "app/shared/auth/private-route";
import {AUTHORITIES} from "app/config/constants";

const Routes = ({ match }) => (
  <Switch>
    <PrivateRoute path={match.url} component={ConfigurationCustomer} hasAnyAuthorities={[AUTHORITIES.ADMIN_FONC]}/>
  </Switch>
);

export default Routes;
