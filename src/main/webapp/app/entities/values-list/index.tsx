import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ValuesList from './values-list';
import ValuesListDetail from './values-list-detail';
import ValuesListUpdate from './values-list-update';
import ValuesListDeleteDialog from './values-list-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ValuesListUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ValuesListUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ValuesListDetail} />
      <ErrorBoundaryRoute path={match.url} component={ValuesList} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={ValuesListDeleteDialog} />
  </>
);

export default Routes;
