import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudSearchAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, IWorkflowState } from 'app/shared/model/workflow-state.model';

export const ACTION_TYPES = {
  SEARCH_WORKFLOWSTATES: 'workflowState/SEARCH_WORKFLOWSTATES',
  FETCH_WORKFLOWSTATE_LIST: 'workflowState/FETCH_WORKFLOWSTATE_LIST',
  FETCH_WORKFLOWSTATE: 'workflowState/FETCH_WORKFLOWSTATE',
  CREATE_WORKFLOWSTATE: 'workflowState/CREATE_WORKFLOWSTATE',
  UPDATE_WORKFLOWSTATE: 'workflowState/UPDATE_WORKFLOWSTATE',
  DELETE_WORKFLOWSTATE: 'workflowState/DELETE_WORKFLOWSTATE',
  RESET: 'workflowState/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IWorkflowState>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type WorkflowStateState = Readonly<typeof initialState>;

// Reducer

export default (state: WorkflowStateState = initialState, action): WorkflowStateState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_WORKFLOWSTATES):
    case REQUEST(ACTION_TYPES.FETCH_WORKFLOWSTATE_LIST):
    case REQUEST(ACTION_TYPES.FETCH_WORKFLOWSTATE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_WORKFLOWSTATE):
    case REQUEST(ACTION_TYPES.UPDATE_WORKFLOWSTATE):
    case REQUEST(ACTION_TYPES.DELETE_WORKFLOWSTATE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_WORKFLOWSTATES):
    case FAILURE(ACTION_TYPES.FETCH_WORKFLOWSTATE_LIST):
    case FAILURE(ACTION_TYPES.FETCH_WORKFLOWSTATE):
    case FAILURE(ACTION_TYPES.CREATE_WORKFLOWSTATE):
    case FAILURE(ACTION_TYPES.UPDATE_WORKFLOWSTATE):
    case FAILURE(ACTION_TYPES.DELETE_WORKFLOWSTATE):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_WORKFLOWSTATES):
    case SUCCESS(ACTION_TYPES.FETCH_WORKFLOWSTATE_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_WORKFLOWSTATE):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_WORKFLOWSTATE):
    case SUCCESS(ACTION_TYPES.UPDATE_WORKFLOWSTATE):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_WORKFLOWSTATE):
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

const apiUrl = 'api/workflow-states';
const apiSearchUrl = 'api/_search/workflow-states';

// Actions

export const getSearchEntities: ICrudSearchAction<IWorkflowState> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_WORKFLOWSTATES,
  payload: axios.get<IWorkflowState>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<IWorkflowState> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_WORKFLOWSTATE_LIST,
  payload: axios.get<IWorkflowState>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IWorkflowState> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_WORKFLOWSTATE,
    payload: axios.get<IWorkflowState>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IWorkflowState> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_WORKFLOWSTATE,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IWorkflowState> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_WORKFLOWSTATE,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IWorkflowState> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_WORKFLOWSTATE,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
