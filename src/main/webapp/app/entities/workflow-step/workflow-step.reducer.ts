import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudSearchAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, IWorkflowStep } from 'app/shared/model/workflow-step.model';

export const ACTION_TYPES = {
  SEARCH_WORKFLOWSTEPS: 'workflowStep/SEARCH_WORKFLOWSTEPS',
  FETCH_WORKFLOWSTEP_LIST: 'workflowStep/FETCH_WORKFLOWSTEP_LIST',
  FETCH_WORKFLOWSTEP: 'workflowStep/FETCH_WORKFLOWSTEP',
  CREATE_WORKFLOWSTEP: 'workflowStep/CREATE_WORKFLOWSTEP',
  UPDATE_WORKFLOWSTEP: 'workflowStep/UPDATE_WORKFLOWSTEP',
  DELETE_WORKFLOWSTEP: 'workflowStep/DELETE_WORKFLOWSTEP',
  RESET: 'workflowStep/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IWorkflowStep>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type WorkflowStepState = Readonly<typeof initialState>;

// Reducer

export default (state: WorkflowStepState = initialState, action): WorkflowStepState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_WORKFLOWSTEPS):
    case REQUEST(ACTION_TYPES.FETCH_WORKFLOWSTEP_LIST):
    case REQUEST(ACTION_TYPES.FETCH_WORKFLOWSTEP):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_WORKFLOWSTEP):
    case REQUEST(ACTION_TYPES.UPDATE_WORKFLOWSTEP):
    case REQUEST(ACTION_TYPES.DELETE_WORKFLOWSTEP):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_WORKFLOWSTEPS):
    case FAILURE(ACTION_TYPES.FETCH_WORKFLOWSTEP_LIST):
    case FAILURE(ACTION_TYPES.FETCH_WORKFLOWSTEP):
    case FAILURE(ACTION_TYPES.CREATE_WORKFLOWSTEP):
    case FAILURE(ACTION_TYPES.UPDATE_WORKFLOWSTEP):
    case FAILURE(ACTION_TYPES.DELETE_WORKFLOWSTEP):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_WORKFLOWSTEPS):
    case SUCCESS(ACTION_TYPES.FETCH_WORKFLOWSTEP_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_WORKFLOWSTEP):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_WORKFLOWSTEP):
    case SUCCESS(ACTION_TYPES.UPDATE_WORKFLOWSTEP):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_WORKFLOWSTEP):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/workflow-steps';
const apiSearchUrl = 'api/_search/workflow-steps';

// Actions

export const getSearchEntities: ICrudSearchAction<IWorkflowStep> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_WORKFLOWSTEPS,
  payload: axios.get<IWorkflowStep>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<IWorkflowStep> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_WORKFLOWSTEP_LIST,
  payload: axios.get<IWorkflowStep>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IWorkflowStep> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_WORKFLOWSTEP,
    payload: axios.get<IWorkflowStep>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IWorkflowStep> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_WORKFLOWSTEP,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IWorkflowStep> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_WORKFLOWSTEP,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IWorkflowStep> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_WORKFLOWSTEP,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
