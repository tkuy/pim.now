import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import AttributValue from './attribut-value';
import AttributValueDetail from './attribut-value-detail';
import AttributValueUpdate from './attribut-value-update';
import AttributValueDeleteDialog from './attribut-value-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={AttributValueUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={AttributValueUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={AttributValueDetail} />
      <ErrorBoundaryRoute path={match.url} component={AttributValue} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={AttributValueDeleteDialog} />
  </>
);

export default Routes;
