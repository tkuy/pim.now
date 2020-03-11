import React from 'react';
import {Switch} from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ValuesListItem from './values-list-item';
import ValuesListItemDetail from './values-list-item-detail';
import ValuesListItemUpdate from './values-list-item-update';
import ValuesListItemDeleteDialog from './values-list-item-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ValuesListItemUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ValuesListItemUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ValuesListItemDetail} />
      <ErrorBoundaryRoute path={match.url} component={ValuesListItem} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={ValuesListItemDeleteDialog} />
  </>
);

export default Routes;
