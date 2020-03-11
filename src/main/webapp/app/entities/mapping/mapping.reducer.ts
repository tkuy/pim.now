import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudSearchAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, defaultValueDup, IMapping, IMappingDuplicate } from 'app/shared/model/mapping.model';

export const ACTION_TYPES = {
  SEARCH_MAPPINGS: 'mapping/SEARCH_MAPPINGS',
  FETCH_MAPPING_LIST: 'mapping/FETCH_MAPPING_LIST',
  FETCH_MAPPING: 'mapping/FETCH_MAPPING',
  CREATE_MAPPING: 'mapping/CREATE_MAPPING',
  UPDATE_MAPPING: 'mapping/UPDATE_MAPPING',
  DELETE_MAPPING: 'mapping/DELETE_MAPPING',
  RESET: 'mapping/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IMapping>,
  entity: defaultValue,
  entityDup: defaultValueDup,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type MappingState = Readonly<typeof initialState>;

// Reducer

export default (state: MappingState = initialState, action): MappingState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_MAPPINGS):
    case REQUEST(ACTION_TYPES.FETCH_MAPPING_LIST):
    case REQUEST(ACTION_TYPES.FETCH_MAPPING):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_MAPPING):
    case REQUEST(ACTION_TYPES.UPDATE_MAPPING):
    case REQUEST(ACTION_TYPES.DELETE_MAPPING):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_MAPPINGS):
    case FAILURE(ACTION_TYPES.FETCH_MAPPING_LIST):
    case FAILURE(ACTION_TYPES.FETCH_MAPPING):
    case FAILURE(ACTION_TYPES.CREATE_MAPPING):
    case FAILURE(ACTION_TYPES.UPDATE_MAPPING):
    case FAILURE(ACTION_TYPES.DELETE_MAPPING):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_MAPPINGS):
    case SUCCESS(ACTION_TYPES.FETCH_MAPPING_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10)
      };
    case SUCCESS(ACTION_TYPES.FETCH_MAPPING):
      return {
        ...state,
        loading: false,
        entityDup: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_MAPPING):
    case SUCCESS(ACTION_TYPES.UPDATE_MAPPING):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_MAPPING):
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

const apiUrl = 'api/mappings';
const apiSearchUrl = 'api/_search/mappings';

// Actions

export const getSearchEntities: ICrudSearchAction<IMapping> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_MAPPINGS,
  payload: axios.get<IMapping>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllAction<IMapping> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_MAPPING_LIST,
    payload: axios.get<IMapping>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IMapping> = id => {
  const requestUrl = `${apiUrl}/duplicate/${id}`;
  return {
    type: ACTION_TYPES.FETCH_MAPPING,
    payload: axios.get<IMappingDuplicate>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IMapping> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_MAPPING,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IMapping> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_MAPPING,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IMapping> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_MAPPING,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
