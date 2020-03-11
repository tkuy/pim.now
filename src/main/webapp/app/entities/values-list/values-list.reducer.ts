import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudSearchAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, IValuesList } from 'app/shared/model/values-list.model';

export const ACTION_TYPES = {
  SEARCH_VALUESLISTS: 'valuesList/SEARCH_VALUESLISTS',
  FETCH_VALUESLIST_LIST: 'valuesList/FETCH_VALUESLIST_LIST',
  FETCH_VALUESLIST: 'valuesList/FETCH_VALUESLIST',
  CREATE_VALUESLIST: 'valuesList/CREATE_VALUESLIST',
  UPDATE_VALUESLIST: 'valuesList/UPDATE_VALUESLIST',
  DELETE_VALUESLIST: 'valuesList/DELETE_VALUESLIST',
  RESET: 'valuesList/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IValuesList>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type ValuesListState = Readonly<typeof initialState>;

// Reducer

export default (state: ValuesListState = initialState, action): ValuesListState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_VALUESLISTS):
    case REQUEST(ACTION_TYPES.FETCH_VALUESLIST_LIST):
    case REQUEST(ACTION_TYPES.FETCH_VALUESLIST):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_VALUESLIST):
    case REQUEST(ACTION_TYPES.UPDATE_VALUESLIST):
    case REQUEST(ACTION_TYPES.DELETE_VALUESLIST):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_VALUESLISTS):
    case FAILURE(ACTION_TYPES.FETCH_VALUESLIST_LIST):
    case FAILURE(ACTION_TYPES.FETCH_VALUESLIST):
    case FAILURE(ACTION_TYPES.CREATE_VALUESLIST):
    case FAILURE(ACTION_TYPES.UPDATE_VALUESLIST):
    case FAILURE(ACTION_TYPES.DELETE_VALUESLIST):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_VALUESLISTS):
    case SUCCESS(ACTION_TYPES.FETCH_VALUESLIST_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_VALUESLIST):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_VALUESLIST):
    case SUCCESS(ACTION_TYPES.UPDATE_VALUESLIST):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_VALUESLIST):
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

const apiUrl = 'api/values-lists';
const apiSearchUrl = 'api/_search/values-lists';

// Actions

export const getSearchEntities: ICrudSearchAction<IValuesList> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_VALUESLISTS,
  payload: axios.get<IValuesList>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<IValuesList> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_VALUESLIST_LIST,
  payload: axios.get<IValuesList>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IValuesList> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_VALUESLIST,
    payload: axios.get<IValuesList>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IValuesList> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_VALUESLIST,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IValuesList> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_VALUESLIST,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IValuesList> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_VALUESLIST,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
