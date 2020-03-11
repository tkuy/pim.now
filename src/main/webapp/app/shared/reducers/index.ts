import { combineReducers } from 'redux';
import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale, { LocaleState } from './locale';
import authentication, { AuthenticationState } from './authentication';
import applicationProfile, { ApplicationProfileState } from './application-profile';

import administration, { AdministrationState } from 'app/modules/administration/administration.reducer';
import userManagement, { UserManagementState } from 'app/modules/administration/user-management/user-management.reducer';
import register, { RegisterState } from 'app/modules/account/register/register.reducer';
import activate, { ActivateState } from 'app/modules/account/activate/activate.reducer';
import password, { PasswordState } from 'app/modules/account/password/password.reducer';
import settings, { SettingsState } from 'app/modules/account/settings/settings.reducer';
import passwordReset, { PasswordResetState } from 'app/modules/account/password-reset/password-reset.reducer';
// prettier-ignore
import family, {FamilyState} from 'app/entities/family/family.reducer';
// prettier-ignore
import attribut, {AttributState} from 'app/entities/attribut/attribut.reducer';
// prettier-ignore
import category, {CategoryState} from 'app/entities/category/category.reducer';
// prettier-ignore
import product, {ProductState} from 'app/entities/product/product.reducer';
// prettier-ignore
import attributValue, {AttributValueState} from 'app/entities/attribut-value/attribut-value.reducer';
// prettier-ignore
import userExtra, {UserExtraState} from 'app/entities/user-extra/user-extra.reducer';
// prettier-ignore
import customer, {CustomerState} from 'app/entities/customer/customer.reducer';
// prettier-ignore
import workflow, {WorkflowState} from 'app/entities/workflow/workflow.reducer';
// prettier-ignore
import workflowStep, {WorkflowStepState} from 'app/entities/workflow-step/workflow-step.reducer';
// prettier-ignore
import mapping, {MappingState} from 'app/entities/mapping/mapping.reducer';
// prettier-ignore
import association, {AssociationState} from 'app/entities/association/association.reducer';
// prettier-ignore
import configurationCustomer, {ConfigurationCustomerState} from 'app/entities/configuration-customer/configuration-customer.reducer';
// prettier-ignore
import valuesList, {ValuesListState} from 'app/entities/values-list/values-list.reducer';
// prettier-ignore
import valuesListItem, {ValuesListItemState} from 'app/entities/values-list-item/values-list-item.reducer';
// prettier-ignore
import workflowState, {WorkflowStateState} from 'app/entities/workflow-state/workflow-state.reducer';
// prettier-ignore
import attributValuesList, {AttributValuesListState} from 'app/entities/attribut-values-list/attribut-values-list.reducer';
// prettier-ignore
import prestashopProduct, {
  PrestashopProductState
} from 'app/entities/prestashop-product/prestashop-product.reducer';
// prettier-ignore
import dashboard, {DashboardState} from 'app/shared/reducers/dashboard-reducer';
// prettier-ignore
import importProduct, { ImportProductState } from 'app/entities/import/import.reducer';
import exportProduct, { ExportProductState } from 'app/entities/product/export.reducer';

/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

export interface IRootState {
  readonly authentication: AuthenticationState;
  readonly dashboard: DashboardState;
  readonly locale: LocaleState;
  readonly applicationProfile: ApplicationProfileState;
  readonly administration: AdministrationState;
  readonly userManagement: UserManagementState;
  readonly register: RegisterState;
  readonly activate: ActivateState;
  readonly passwordReset: PasswordResetState;
  readonly password: PasswordState;
  readonly settings: SettingsState;
  readonly family: FamilyState;
  readonly attribut: AttributState;
  readonly category: CategoryState;
  readonly product: ProductState;
  readonly attributValue: AttributValueState;
  readonly userExtra: UserExtraState;
  readonly customer: CustomerState;
  readonly workflow: WorkflowState;
  readonly workflowStep: WorkflowStepState;
  readonly mapping: MappingState;
  readonly association: AssociationState;
  readonly configurationCustomer: ConfigurationCustomerState;
  readonly valuesList: ValuesListState;
  readonly valuesListItem: ValuesListItemState;
  readonly workflowState: WorkflowStateState;
  readonly attributValuesList: AttributValuesListState;
  readonly prestashopProduct: PrestashopProductState;
  readonly importProduct: ImportProductState;
  readonly exportProduct: ExportProductState;
  /* jhipster-needle-add-reducer-type - JHipster will add reducer type here */
  readonly loadingBar: any;
}

const rootReducer = combineReducers<IRootState>({
  authentication,
  dashboard,
  locale,
  applicationProfile,
  administration,
  userManagement,
  register,
  activate,
  passwordReset,
  password,
  settings,
  family,
  attribut,
  category,
  product,
  attributValue,
  userExtra,
  customer,
  workflow,
  workflowStep,
  mapping,
  association,
  configurationCustomer,
  valuesList,
  valuesListItem,
  workflowState,
  attributValuesList,
  prestashopProduct,
  importProduct,
  exportProduct,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar
});

export default rootReducer;
