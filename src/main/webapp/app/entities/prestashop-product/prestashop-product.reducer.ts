import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudSearchAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, IPrestashopProduct } from 'app/shared/model/prestashop-product.model';

export const ACTION_TYPES = {
  SEARCH_PRESTASHOPPRODUCTS: 'prestashopProduct/SEARCH_PRESTASHOPPRODUCTS',
  FETCH_PRESTASHOPPRODUCT_LIST: 'prestashopProduct/FETCH_PRESTASHOPPRODUCT_LIST',
  FETCH_PRESTASHOPPRODUCT: 'prestashopProduct/FETCH_PRESTASHOPPRODUCT',
  CREATE_PRESTASHOPPRODUCT: 'prestashopProduct/CREATE_PRESTASHOPPRODUCT',
  UPDATE_PRESTASHOPPRODUCT: 'prestashopProduct/UPDATE_PRESTASHOPPRODUCT',
  DELETE_PRESTASHOPPRODUCT: 'prestashopProduct/DELETE_PRESTASHOPPRODUCT',
  RESET: 'prestashopProduct/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IPrestashopProduct>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type PrestashopProductState = Readonly<typeof initialState>;

// Reducer

export default (state: PrestashopProductState = initialState, action): PrestashopProductState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_PRESTASHOPPRODUCTS):
    case REQUEST(ACTION_TYPES.FETCH_PRESTASHOPPRODUCT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_PRESTASHOPPRODUCT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_PRESTASHOPPRODUCT):
    case REQUEST(ACTION_TYPES.UPDATE_PRESTASHOPPRODUCT):
    case REQUEST(ACTION_TYPES.DELETE_PRESTASHOPPRODUCT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_PRESTASHOPPRODUCTS):
    case FAILURE(ACTION_TYPES.FETCH_PRESTASHOPPRODUCT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_PRESTASHOPPRODUCT):
    case FAILURE(ACTION_TYPES.CREATE_PRESTASHOPPRODUCT):
    case FAILURE(ACTION_TYPES.UPDATE_PRESTASHOPPRODUCT):
    case FAILURE(ACTION_TYPES.DELETE_PRESTASHOPPRODUCT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_PRESTASHOPPRODUCTS):
    case SUCCESS(ACTION_TYPES.FETCH_PRESTASHOPPRODUCT_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_PRESTASHOPPRODUCT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_PRESTASHOPPRODUCT):
    case SUCCESS(ACTION_TYPES.UPDATE_PRESTASHOPPRODUCT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_PRESTASHOPPRODUCT):
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

const apiUrl = 'api/prestashop-products';
const apiSearchUrl = 'api/_search/prestashop-products';

// Actions

export const getSearchEntities: ICrudSearchAction<IPrestashopProduct> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_PRESTASHOPPRODUCTS,
  payload: axios.get<IPrestashopProduct>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<IPrestashopProduct> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_PRESTASHOPPRODUCT_LIST,
  payload: axios.get<IPrestashopProduct>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IPrestashopProduct> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_PRESTASHOPPRODUCT,
    payload: axios.get<IPrestashopProduct>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IPrestashopProduct> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_PRESTASHOPPRODUCT,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IPrestashopProduct> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_PRESTASHOPPRODUCT,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IPrestashopProduct> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_PRESTASHOPPRODUCT,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
