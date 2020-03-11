import { IWorkflow } from 'app/shared/model/workflow.model';
import { IWorkflowStep } from 'app/shared/model/workflow-step.model';
import { IWorkflowState } from 'app/shared/model/workflow-state.model';

export interface IWorkflowStep {
  id?: number;
  idF?: string;
  name?: string;
  description?: string;
  rank?: number;
  isIntegrationStep?: boolean;
  workflow?: IWorkflow;
  successor?: IWorkflowStep;
  workflowState?: IWorkflowState;
}

export const defaultValue: Readonly<IWorkflowStep> = {
  isIntegrationStep: false
};
