import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import WorkflowState from './workflow-state';
import WorkflowStateDetail from './workflow-state-detail';
import WorkflowStateUpdate from './workflow-state-update';
import WorkflowStateDeleteDialog from './workflow-state-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={WorkflowStateUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={WorkflowStateUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={WorkflowStateDetail} />
      <ErrorBoundaryRoute path={match.url} component={WorkflowState} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={WorkflowStateDeleteDialog} />
  </>
);

export default Routes;
