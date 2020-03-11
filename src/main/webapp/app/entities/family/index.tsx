import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Family from './family';
import FamilyDetail from './family-detail';
import FamilyUpdate from './family-update';
import FamilyDeleteDialog from './family-delete-dialog';
import {AUTHORITIES} from "app/config/constants";
import PrivateRoute from "app/shared/auth/private-route";

const Routes = ({ match }) => (
  <>
    <Switch>
      <PrivateRoute exact path={`${match.url}/new`} component={FamilyUpdate} hasAnyAuthorities={[AUTHORITIES.ADMIN_FONC]} />
      <PrivateRoute exact path={`${match.url}/:id/edit`} component={FamilyUpdate} hasAnyAuthorities={[AUTHORITIES.ADMIN_FONC]}/>
      <ErrorBoundaryRoute path={match.url} component={Family} />
    </Switch>
    <PrivateRoute path={`${match.url}/:id/delete`} component={FamilyDeleteDialog} hasAnyAuthorities={[AUTHORITIES.ADMIN_FONC]} />
  </>
);

export default Routes;
