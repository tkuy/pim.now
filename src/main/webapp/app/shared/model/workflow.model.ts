import { IWorkflowStep } from 'app/shared/model/workflow-step.model';
import { ICustomer } from 'app/shared/model/customer.model';

export interface IWorkflow {
  id?: number;
  idF?: string;
  name?: string;
  description?: string;
  steps?: IWorkflowStep[];
  customer?: ICustomer;
}

export const defaultValue: Readonly<IWorkflow> = {};
