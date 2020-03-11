import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudSearchAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, IAttributValue } from 'app/shared/model/attribut-value.model';

export const ACTION_TYPES = {
  SEARCH_ATTRIBUTVALUES: 'attributValue/SEARCH_ATTRIBUTVALUES',
  FETCH_ATTRIBUTVALUE_LIST: 'attributValue/FETCH_ATTRIBUTVALUE_LIST',
  FETCH_ATTRIBUTVALUE: 'attributValue/FETCH_ATTRIBUTVALUE',
  CREATE_ATTRIBUTVALUE: 'attributValue/CREATE_ATTRIBUTVALUE',
  UPDATE_ATTRIBUTVALUE: 'attributValue/UPDATE_ATTRIBUTVALUE',
  DELETE_ATTRIBUTVALUE: 'attributValue/DELETE_ATTRIBUTVALUE',
  RESET: 'attributValue/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IAttributValue>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type AttributValueState = Readonly<typeof initialState>;

// Reducer

export default (state: AttributValueState = initialState, action): AttributValueState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_ATTRIBUTVALUES):
    case REQUEST(ACTION_TYPES.FETCH_ATTRIBUTVALUE_LIST):
    case REQUEST(ACTION_TYPES.FETCH_ATTRIBUTVALUE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_ATTRIBUTVALUE):
    case REQUEST(ACTION_TYPES.UPDATE_ATTRIBUTVALUE):
    case REQUEST(ACTION_TYPES.DELETE_ATTRIBUTVALUE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_ATTRIBUTVALUES):
    case FAILURE(ACTION_TYPES.FETCH_ATTRIBUTVALUE_LIST):
    case FAILURE(ACTION_TYPES.FETCH_ATTRIBUTVALUE):
    case FAILURE(ACTION_TYPES.CREATE_ATTRIBUTVALUE):
    case FAILURE(ACTION_TYPES.UPDATE_ATTRIBUTVALUE):
    case FAILURE(ACTION_TYPES.DELETE_ATTRIBUTVALUE):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_ATTRIBUTVALUES):
    case SUCCESS(ACTION_TYPES.FETCH_ATTRIBUTVALUE_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10)
      };
    case SUCCESS(ACTION_TYPES.FETCH_ATTRIBUTVALUE):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_ATTRIBUTVALUE):
    case SUCCESS(ACTION_TYPES.UPDATE_ATTRIBUTVALUE):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_ATTRIBUTVALUE):
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

const apiUrl = 'api/attribut-values';
const apiSearchUrl = 'api/_search/attribut-values';

// Actions

export const getSearchEntities: ICrudSearchAction<IAttributValue> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_ATTRIBUTVALUES,
  payload: axios.get<IAttributValue>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<IAttributValue> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_ATTRIBUTVALUE_LIST,
    payload: axios.get<IAttributValue>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IAttributValue> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_ATTRIBUTVALUE,
    payload: axios.get<IAttributValue>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IAttributValue> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_ATTRIBUTVALUE,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IAttributValue> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_ATTRIBUTVALUE,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IAttributValue> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_ATTRIBUTVALUE,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
