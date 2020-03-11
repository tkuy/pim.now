import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Attribut from './attribut';
import AttributDetail from './attribut-detail';
import AttributUpdate from './attribut-update';
import AttributDeleteDialog from './attribut-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={AttributUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={AttributUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={AttributDetail} />
      <ErrorBoundaryRoute path={match.url} component={Attribut} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={AttributDeleteDialog} />
  </>
);

export default Routes;
