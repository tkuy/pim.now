import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudSearchAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, IValuesListItem } from 'app/shared/model/values-list-item.model';

export const ACTION_TYPES = {
  SEARCH_VALUESLISTITEMS: 'valuesListItem/SEARCH_VALUESLISTITEMS',
  FETCH_VALUESLISTITEM_LIST: 'valuesListItem/FETCH_VALUESLISTITEM_LIST',
  FETCH_VALUESLISTITEM: 'valuesListItem/FETCH_VALUESLISTITEM',
  CREATE_VALUESLISTITEM: 'valuesListItem/CREATE_VALUESLISTITEM',
  UPDATE_VALUESLISTITEM: 'valuesListItem/UPDATE_VALUESLISTITEM',
  DELETE_VALUESLISTITEM: 'valuesListItem/DELETE_VALUESLISTITEM',
  RESET: 'valuesListItem/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IValuesListItem>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type ValuesListItemState = Readonly<typeof initialState>;

// Reducer

export default (state: ValuesListItemState = initialState, action): ValuesListItemState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_VALUESLISTITEMS):
    case REQUEST(ACTION_TYPES.FETCH_VALUESLISTITEM_LIST):
    case REQUEST(ACTION_TYPES.FETCH_VALUESLISTITEM):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_VALUESLISTITEM):
    case REQUEST(ACTION_TYPES.UPDATE_VALUESLISTITEM):
    case REQUEST(ACTION_TYPES.DELETE_VALUESLISTITEM):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_VALUESLISTITEMS):
    case FAILURE(ACTION_TYPES.FETCH_VALUESLISTITEM_LIST):
    case FAILURE(ACTION_TYPES.FETCH_VALUESLISTITEM):
    case FAILURE(ACTION_TYPES.CREATE_VALUESLISTITEM):
    case FAILURE(ACTION_TYPES.UPDATE_VALUESLISTITEM):
    case FAILURE(ACTION_TYPES.DELETE_VALUESLISTITEM):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_VALUESLISTITEMS):
    case SUCCESS(ACTION_TYPES.FETCH_VALUESLISTITEM_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_VALUESLISTITEM):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_VALUESLISTITEM):
    case SUCCESS(ACTION_TYPES.UPDATE_VALUESLISTITEM):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_VALUESLISTITEM):
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

const apiUrl = 'api/values-list-items';
const apiSearchUrl = 'api/_search/values-list-items';

// Actions

export const getSearchEntities: ICrudSearchAction<IValuesListItem> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_VALUESLISTITEMS,
  payload: axios.get<IValuesListItem>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<IValuesListItem> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_VALUESLISTITEM_LIST,
  payload: axios.get<IValuesListItem>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IValuesListItem> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_VALUESLISTITEM,
    payload: axios.get<IValuesListItem>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IValuesListItem> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_VALUESLISTITEM,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IValuesListItem> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_VALUESLISTITEM,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IValuesListItem> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_VALUESLISTITEM,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
