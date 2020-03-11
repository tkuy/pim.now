import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, ICategory } from 'app/shared/model/category.model';

export const ACTION_TYPES = {
  FETCH_CATEGORY_LIST: 'category/FETCH_CATEGORY_LIST',
  FETCH_CATEGORY: 'category/FETCH_CATEGORY',
  FETCH_CATEGORY_PREDECESSOR: 'category/FETCH_CATEGORY_PREDECESSOR',
  FETCH_CATEGORY_ROOT: 'category/FETCH_CATEGORY_ROOT',
  CREATE_CATEGORY: 'category/CREATE_CATEGORY',
  UPDATE_CATEGORY: 'category/UPDATE_CATEGORY',
  DELETE_CATEGORY: 'category/DELETE_CATEGORY',
  RESET: 'category/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ICategory>,
  entity: defaultValue,
  entityRoot: defaultValue,
  updating: false,
  updateSuccess: false,
  entityPredecessor: 0
};

export type CategoryState = Readonly<typeof initialState>;

// Reducer

export default (state: CategoryState = initialState, action): CategoryState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_CATEGORY_LIST):
    case REQUEST(ACTION_TYPES.FETCH_CATEGORY):
    case REQUEST(ACTION_TYPES.FETCH_CATEGORY_PREDECESSOR):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.FETCH_CATEGORY_ROOT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_CATEGORY):
    case REQUEST(ACTION_TYPES.UPDATE_CATEGORY):
    case REQUEST(ACTION_TYPES.DELETE_CATEGORY):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_CATEGORY_LIST):
    case FAILURE(ACTION_TYPES.FETCH_CATEGORY):
    case FAILURE(ACTION_TYPES.FETCH_CATEGORY_ROOT):
    case FAILURE(ACTION_TYPES.CREATE_CATEGORY):
    case FAILURE(ACTION_TYPES.UPDATE_CATEGORY):
    case FAILURE(ACTION_TYPES.DELETE_CATEGORY):
    case FAILURE(ACTION_TYPES.FETCH_CATEGORY_PREDECESSOR):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_CATEGORY_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_CATEGORY):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_CATEGORY_PREDECESSOR):
      return {
        ...state,
        loading: false,
        entityPredecessor: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_CATEGORY_ROOT):
      return {
        ...state,
        loading: false,
        entityRoot: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_CATEGORY):
    case SUCCESS(ACTION_TYPES.UPDATE_CATEGORY):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_CATEGORY):
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

const apiUrl = 'api/categories';

// Actions

export const getEntities: ICrudGetAllAction<ICategory> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_CATEGORY_LIST,
  payload: axios.get<ICategory>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<ICategory> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_CATEGORY,
    payload: axios.get<ICategory>(requestUrl)
  };
};

export const getEntityPredecessor: ICrudGetAction<number> = id => {
  const requestUrl = `${apiUrl}/predecessor/${id}`;
  return {
    type: ACTION_TYPES.FETCH_CATEGORY_PREDECESSOR,
    payload: axios.get<number>(requestUrl)
  };
};

export const getEntityRoot: ICrudGetAction<ICategory> = id => {
  const requestUrl = `${apiUrl}/root`;
  return {
    type: ACTION_TYPES.FETCH_CATEGORY_ROOT,
    payload: axios.get<ICategory>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<ICategory> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_CATEGORY,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ICategory> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_CATEGORY,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<ICategory> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_CATEGORY,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());

  // Need to be changed, used to hide deleted category
  window.location.reload();

  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
