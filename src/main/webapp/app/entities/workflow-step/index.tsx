import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import WorkflowStep from './workflow-step';
import WorkflowStepDetail from './workflow-step-detail';
import WorkflowStepUpdate from './workflow-step-update';
import WorkflowStepDeleteDialog from './workflow-step-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={WorkflowStepUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={WorkflowStepUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={WorkflowStepDetail} />
      <ErrorBoundaryRoute path={match.url} component={WorkflowStep} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={WorkflowStepDeleteDialog} />
  </>
);

export default Routes;
