import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import AttributValuesList from './attribut-values-list';
import AttributValuesListDetail from './attribut-values-list-detail';
import AttributValuesListUpdate from './attribut-values-list-update';
import AttributValuesListDeleteDialog from './attribut-values-list-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={AttributValuesListUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={AttributValuesListUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={AttributValuesListDetail} />
      <ErrorBoundaryRoute path={match.url} component={AttributValuesList} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={AttributValuesListDeleteDialog} />
  </>
);

export default Routes;
