import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Mapping from './mapping';
import MappingUpdate from './mapping-update';
import MappingDeleteDialog from './mapping-delete-dialog';
import PrivateRoute from "app/shared/auth/private-route";
import {AUTHORITIES} from "app/config/constants";

const Routes = ({ match }) => (
  <>
    <Switch>
      <PrivateRoute exact path={`${match.url}/new`} component={MappingUpdate} hasAnyAuthorities={[AUTHORITIES.ADMIN_FONC]}/>
      <PrivateRoute exact path={`${match.url}/info/:id`} component={MappingUpdate} hasAnyAuthorities={[AUTHORITIES.ADMIN_FONC, AUTHORITIES.USER]}/>
      <PrivateRoute exact path={`${match.url}/:id/edit`} component={MappingUpdate} hasAnyAuthorities={[AUTHORITIES.ADMIN_FONC]}/>
      <PrivateRoute exact path={`${match.url}/duplicate/:id`} component={MappingUpdate} hasAnyAuthorities={[AUTHORITIES.ADMIN_FONC]}/>
      <ErrorBoundaryRoute path={match.url} component={Mapping}/>
      <ErrorBoundaryRoute exact path={`${match.url}/:type/:id`} component={MappingUpdate} />
    </Switch>
    <PrivateRoute path={`${match.url}/:id/delete`} component={MappingDeleteDialog} hasAnyAuthorities={[AUTHORITIES.ADMIN_FONC]}/>
  </>
);

export default Routes;
