import { IWorkflowStep } from 'app/shared/model/workflow-step.model';
import { IProduct } from 'app/shared/model/product.model';
import { EnumWorkflowState } from 'app/shared/model/enumerations/enum-workflow-state.model';

export interface IWorkflowState {
  id?: number;
  state?: EnumWorkflowState;
  failDescription?: string;
  steps?: IWorkflowStep[];
  product?: IProduct;
}

export const defaultValue: Readonly<IWorkflowState> = {};
