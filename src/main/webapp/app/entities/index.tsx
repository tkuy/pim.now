import React from 'react';
import {Switch} from 'react-router-dom';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Family from './family';
import Attribut from './attribut';
import Category from './category';
import Product from './product';
import AttributValue from './attribut-value';
import UserExtra from './user-extra';
import Customer from './customer';
import Workflow from './workflow';
import WorkflowStep from './workflow-step';
import Mapping from './mapping';
import Association from './association';
import ConfigurationCustomer from './configuration-customer';
import ValuesList from './values-list';
import ValuesListItem from './values-list-item';
import WorkflowState from './workflow-state';
import AttributValuesList from './attribut-values-list';
import PrestashopProduct from './prestashop-product';
import ImportProduct from "./import";
import {FamilyUpdate} from "app/entities/family/family-update";
import PrivateRoute from "app/shared/auth/private-route";
import {AUTHORITIES} from "app/config/constants";
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}/family`} component={Family} />
      <ErrorBoundaryRoute path={`${match.url}/category`} component={Category} />
      <ErrorBoundaryRoute path={`${match.url}/product`} component={Product} />
      <ErrorBoundaryRoute path={`${match.url}/user-extra`} component={UserExtra} />
      <ErrorBoundaryRoute path={`${match.url}/customer`} component={Customer} />
      <ErrorBoundaryRoute path={`${match.url}/mapping`} component={Mapping} />
      <ErrorBoundaryRoute path={`${match.url}/configuration-customer`} component={ConfigurationCustomer} />
      <ErrorBoundaryRoute path={`${match.url}/workflow-state`} component={WorkflowState} />
      <ErrorBoundaryRoute path={`${match.url}/import`} component={ImportProduct} />
      {/* <ErrorBoundaryRoute path={`${match.url}/workflow`} component={Workflow} /> */}
      {/* <ErrorBoundaryRoute path={`${match.url}/values-list`} component={ValuesList} /> */}
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
