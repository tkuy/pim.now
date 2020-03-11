import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudSearchAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, IAttributValuesList } from 'app/shared/model/attribut-values-list.model';

export const ACTION_TYPES = {
  SEARCH_ATTRIBUTVALUESLISTS: 'attributValuesList/SEARCH_ATTRIBUTVALUESLISTS',
  FETCH_ATTRIBUTVALUESLIST_LIST: 'attributValuesList/FETCH_ATTRIBUTVALUESLIST_LIST',
  FETCH_ATTRIBUTVALUESLIST: 'attributValuesList/FETCH_ATTRIBUTVALUESLIST',
  CREATE_ATTRIBUTVALUESLIST: 'attributValuesList/CREATE_ATTRIBUTVALUESLIST',
  UPDATE_ATTRIBUTVALUESLIST: 'attributValuesList/UPDATE_ATTRIBUTVALUESLIST',
  DELETE_ATTRIBUTVALUESLIST: 'attributValuesList/DELETE_ATTRIBUTVALUESLIST',
  RESET: 'attributValuesList/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IAttributValuesList>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type AttributValuesListState = Readonly<typeof initialState>;

// Reducer

export default (state: AttributValuesListState = initialState, action): AttributValuesListState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_ATTRIBUTVALUESLISTS):
    case REQUEST(ACTION_TYPES.FETCH_ATTRIBUTVALUESLIST_LIST):
    case REQUEST(ACTION_TYPES.FETCH_ATTRIBUTVALUESLIST):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_ATTRIBUTVALUESLIST):
    case REQUEST(ACTION_TYPES.UPDATE_ATTRIBUTVALUESLIST):
    case REQUEST(ACTION_TYPES.DELETE_ATTRIBUTVALUESLIST):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_ATTRIBUTVALUESLISTS):
    case FAILURE(ACTION_TYPES.FETCH_ATTRIBUTVALUESLIST_LIST):
    case FAILURE(ACTION_TYPES.FETCH_ATTRIBUTVALUESLIST):
    case FAILURE(ACTION_TYPES.CREATE_ATTRIBUTVALUESLIST):
    case FAILURE(ACTION_TYPES.UPDATE_ATTRIBUTVALUESLIST):
    case FAILURE(ACTION_TYPES.DELETE_ATTRIBUTVALUESLIST):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_ATTRIBUTVALUESLISTS):
    case SUCCESS(ACTION_TYPES.FETCH_ATTRIBUTVALUESLIST_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_ATTRIBUTVALUESLIST):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_ATTRIBUTVALUESLIST):
    case SUCCESS(ACTION_TYPES.UPDATE_ATTRIBUTVALUESLIST):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_ATTRIBUTVALUESLIST):
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

const apiUrl = 'api/attribut-values-lists';
const apiSearchUrl = 'api/_search/attribut-values-lists';

// Actions

export const getSearchEntities: ICrudSearchAction<IAttributValuesList> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_ATTRIBUTVALUESLISTS,
  payload: axios.get<IAttributValuesList>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<IAttributValuesList> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_ATTRIBUTVALUESLIST_LIST,
  payload: axios.get<IAttributValuesList>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IAttributValuesList> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_ATTRIBUTVALUESLIST,
    payload: axios.get<IAttributValuesList>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IAttributValuesList> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_ATTRIBUTVALUESLIST,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IAttributValuesList> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_ATTRIBUTVALUESLIST,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IAttributValuesList> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_ATTRIBUTVALUESLIST,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
