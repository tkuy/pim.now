import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudSearchAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, IFamily } from 'app/shared/model/family.model';
import { IAttribut } from 'app/shared/model/attribut.model';

export const ACTION_TYPES = {
  FETCH_FAMILY_LIST: 'family/FETCH_FAMILY_LIST',
  FETCH_ATTRIBUT_LIST: 'family/FETCH_ATTRIBUT_LIST',
  FETCH_FAMILY: 'family/FETCH_FAMILY',
  FETCH_FAMILY_ROOT: 'family/FETCH_FAMILY_ROOT',
  CREATE_FAMILY: 'family/CREATE_FAMILY',
  UPDATE_FAMILY: 'family/UPDATE_FAMILY',
  DELETE_FAMILY: 'family/DELETE_FAMILY',
  RESET: 'family/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IFamily>,
  entitiesAttributs: [] as ReadonlyArray<IAttribut>,
  entity: defaultValue,
  entityRoot: defaultValue,
  updating: false,
  updateSuccess: false
};

export type FamilyState = Readonly<typeof initialState>;

// Reducer

export default (state: FamilyState = initialState, action): FamilyState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_FAMILY_LIST):
    case REQUEST(ACTION_TYPES.FETCH_ATTRIBUT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_FAMILY):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.FETCH_FAMILY_ROOT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_FAMILY):
    case REQUEST(ACTION_TYPES.UPDATE_FAMILY):
    case REQUEST(ACTION_TYPES.DELETE_FAMILY):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_FAMILY_LIST):
    case FAILURE(ACTION_TYPES.FETCH_ATTRIBUT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_FAMILY):
    case FAILURE(ACTION_TYPES.FETCH_FAMILY_ROOT):
    case FAILURE(ACTION_TYPES.CREATE_FAMILY):
    case FAILURE(ACTION_TYPES.UPDATE_FAMILY):
    case FAILURE(ACTION_TYPES.DELETE_FAMILY):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_FAMILY_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_ATTRIBUT_LIST):
      return {
        ...state,
        loading: false,
        entitiesAttributs: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_FAMILY):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_FAMILY_ROOT):
      return {
        ...state,
        loading: false,
        entityRoot: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_FAMILY):
    case SUCCESS(ACTION_TYPES.UPDATE_FAMILY):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_FAMILY):
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

const apiUrl = 'api/families';
const apiSearchUrl = 'api/_search/families';

// Actions

export const getEntities: ICrudGetAllAction<IFamily> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_FAMILY_LIST,
  payload: axios.get<IFamily>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntitiesAttributs: ICrudGetAction<IAttribut[]> = id => {
  const requestUrl = `${apiUrl}/familyAttributs/${id}`;
  return {
    type: ACTION_TYPES.FETCH_ATTRIBUT_LIST,
    payload: axios.get<IAttribut[]>(requestUrl)
  };
};

export const getEntity: ICrudGetAction<IFamily> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_FAMILY,
    payload: axios.get<IFamily>(requestUrl)
  };
};

export const getEntityRoot: ICrudGetAction<IFamily> = id => {
  const requestUrl = `${apiUrl}/root`;
  return {
    type: ACTION_TYPES.FETCH_FAMILY_ROOT,
    payload: axios.get<IFamily>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IFamily> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_FAMILY,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IFamily> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_FAMILY,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IFamily> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_FAMILY,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());

  // Need to be changed, used to hide deleted family
  window.location.reload();
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
