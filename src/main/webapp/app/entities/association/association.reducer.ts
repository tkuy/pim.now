import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudSearchAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, IAssociation } from 'app/shared/model/association.model';

export const ACTION_TYPES = {
  SEARCH_ASSOCIATIONS: 'association/SEARCH_ASSOCIATIONS',
  FETCH_ASSOCIATION_LIST: 'association/FETCH_ASSOCIATION_LIST',
  FETCH_ASSOCIATION: 'association/FETCH_ASSOCIATION',
  CREATE_ASSOCIATION: 'association/CREATE_ASSOCIATION',
  UPDATE_ASSOCIATION: 'association/UPDATE_ASSOCIATION',
  DELETE_ASSOCIATION: 'association/DELETE_ASSOCIATION',
  RESET: 'association/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IAssociation>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type AssociationState = Readonly<typeof initialState>;

// Reducer

export default (state: AssociationState = initialState, action): AssociationState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_ASSOCIATIONS):
    case REQUEST(ACTION_TYPES.FETCH_ASSOCIATION_LIST):
    case REQUEST(ACTION_TYPES.FETCH_ASSOCIATION):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_ASSOCIATION):
    case REQUEST(ACTION_TYPES.UPDATE_ASSOCIATION):
    case REQUEST(ACTION_TYPES.DELETE_ASSOCIATION):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_ASSOCIATIONS):
    case FAILURE(ACTION_TYPES.FETCH_ASSOCIATION_LIST):
    case FAILURE(ACTION_TYPES.FETCH_ASSOCIATION):
    case FAILURE(ACTION_TYPES.CREATE_ASSOCIATION):
    case FAILURE(ACTION_TYPES.UPDATE_ASSOCIATION):
    case FAILURE(ACTION_TYPES.DELETE_ASSOCIATION):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_ASSOCIATIONS):
    case SUCCESS(ACTION_TYPES.FETCH_ASSOCIATION_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_ASSOCIATION):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_ASSOCIATION):
    case SUCCESS(ACTION_TYPES.UPDATE_ASSOCIATION):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_ASSOCIATION):
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

const apiUrl = 'api/associations';
const apiSearchUrl = 'api/_search/associations';

// Actions

export const getSearchEntities: ICrudSearchAction<IAssociation> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_ASSOCIATIONS,
  payload: axios.get<IAssociation>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<IAssociation> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_ASSOCIATION_LIST,
  payload: axios.get<IAssociation>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IAssociation> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_ASSOCIATION,
    payload: axios.get<IAssociation>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IAssociation> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_ASSOCIATION,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IAssociation> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_ASSOCIATION,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IAssociation> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_ASSOCIATION,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
