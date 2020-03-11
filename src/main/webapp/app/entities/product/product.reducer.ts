import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudSearchAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, IProduct } from 'app/shared/model/product.model';

export const ACTION_TYPES = {
  SEARCH_PRODUCTS: 'product/SEARCH_PRODUCTS',
  SEARCH_PRODUCTS_SELECT_ALL: 'product/SEARCH_PRODUCTS_SELECT_ALL',
  FETCH_PRODUCT_LIST: 'product/FETCH_PRODUCT_LIST',
  FETCH_PRODUCT: 'product/FETCH_PRODUCT',
  FETCH_ALL_PRODUCTS: 'product/FETCH_ALL_PRODUCTS',
  CREATE_PRODUCT: 'product/CREATE_PRODUCT',
  UPDATE_PRODUCT: 'product/UPDATE_PRODUCT',
  DELETE_PRODUCT: 'product/DELETE_PRODUCT',
  INTEGRATE_TO_PRESTASHOP: 'product/INTEGRATE_TO_PRESTASHOP',
  REMOVE_TO_PRESTASHOP: 'products/REMOVE_TO_PRESTASHOP',
  RESET: 'product/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as IProduct[],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
  loadingIntegration: false,
  exportingProduct: false,
  loadingIntegrationRemove: false,
  selectAllList: [] as IProduct[],
  selectAllListWithSearch: [] as IProduct[]
};

export type ProductState = Readonly<typeof initialState>;

// Reducer

export default (state: ProductState = initialState, action): ProductState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_PRODUCTS):
    case REQUEST(ACTION_TYPES.SEARCH_PRODUCTS_SELECT_ALL):
    case REQUEST(ACTION_TYPES.FETCH_PRODUCT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_PRODUCT):
    case REQUEST(ACTION_TYPES.FETCH_ALL_PRODUCTS):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.INTEGRATE_TO_PRESTASHOP):
      return {
        ...state,
        errorMessage: null,
        loadingIntegration: true
      };
    case REQUEST(ACTION_TYPES.REMOVE_TO_PRESTASHOP):
      return {
        ...state,
        errorMessage: null,
        loadingIntegrationRemove: true
      };
    case REQUEST(ACTION_TYPES.CREATE_PRODUCT):
    case REQUEST(ACTION_TYPES.UPDATE_PRODUCT):
    case REQUEST(ACTION_TYPES.DELETE_PRODUCT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_PRODUCTS):
    case FAILURE(ACTION_TYPES.FETCH_PRODUCT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_PRODUCT):
    case FAILURE(ACTION_TYPES.CREATE_PRODUCT):
    case FAILURE(ACTION_TYPES.UPDATE_PRODUCT):
    case FAILURE(ACTION_TYPES.DELETE_PRODUCT):
    case FAILURE(ACTION_TYPES.INTEGRATE_TO_PRESTASHOP):
    case FAILURE(ACTION_TYPES.REMOVE_TO_PRESTASHOP):
    case FAILURE(ACTION_TYPES.FETCH_ALL_PRODUCTS):
    case FAILURE(ACTION_TYPES.SEARCH_PRODUCTS_SELECT_ALL):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
        loadingIntegrationRemove: false,
        loadingIntegration: false
      };
    case SUCCESS(ACTION_TYPES.SEARCH_PRODUCTS):
    case SUCCESS(ACTION_TYPES.FETCH_PRODUCT_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10)
      };
    case SUCCESS(ACTION_TYPES.SEARCH_PRODUCTS_SELECT_ALL):
      return {
        ...state,
        loading: false,
        selectAllListWithSearch: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_ALL_PRODUCTS):
      return {
        ...state,
        loading: false,
        selectAllList: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.INTEGRATE_TO_PRESTASHOP):
      return {
        ...state,
        loadingIntegration: false,
        updateSuccess: true
      };
    case SUCCESS(ACTION_TYPES.REMOVE_TO_PRESTASHOP):
      return {
        ...state,
        loadingIntegrationRemove: false,
        updateSuccess: true
      };
    case SUCCESS(ACTION_TYPES.FETCH_PRODUCT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_PRODUCT):
    case SUCCESS(ACTION_TYPES.UPDATE_PRODUCT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_PRODUCT):
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

const apiUrl = 'api/products';
const apiSearchUrl = 'api/_search/products';
const apiIntegrationToPrestashopUrl = 'api/integrate/products';
const apiRemovePrestashopUrl = 'api/integrate/products/delete';

// Actions
export const getSearchEntities: ICrudSearchAction<IProduct> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_PRODUCTS,
  payload: axios.get<IProduct>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}` : ''}`)
});

export const getSearchEntitiesForSelectAll: ICrudSearchAction<IProduct> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_PRODUCTS_SELECT_ALL,
  payload: axios.get<IProduct>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=10000` : ''}`)
});

export const getEntities: ICrudGetAllAction<IProduct> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_PRODUCT_LIST,
    payload: axios.get<IProduct>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const selectAll: ICrudGetAction<IProduct> = () => {
  return {
    type: ACTION_TYPES.FETCH_ALL_PRODUCTS,
    payload: axios.get<IProduct>(`${apiUrl}/selectAll`)
  };
};

export const getEntity: ICrudGetAction<IProduct> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_PRODUCT,
    payload: axios.get<IProduct>(requestUrl)
  };
};

const TIMEOUT = 20 * 60 * 1000;

export const integrateToPrestashop: ICrudPutAction<IProduct[]> = entities => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.INTEGRATE_TO_PRESTASHOP,
    payload: axios.post(apiIntegrationToPrestashopUrl, entities, { timeout: TIMEOUT })
  });
  dispatch();
  return result;
};

export const removeIntegrationFromPrestashop: ICrudPutAction<IProduct[]> = entities => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.REMOVE_TO_PRESTASHOP,
    payload: axios.post(apiRemovePrestashopUrl, entities, { timeout: TIMEOUT })
  });
  dispatch();
  return result;
};

const formDataFromProduct = (entity: IProduct) => {
  const clean = cleanEntity(entity);
  const newEntity = {
    ...clean,
    files: undefined
  };
  const productFile = new Blob([JSON.stringify(newEntity)], { type: 'application/json', endings: 'native' });
  const formData = new FormData();
  formData.append('productVM', productFile);
  entity.files && entity.files.forEach(file => formData.append('files', file));
  return formData;
};

export const createEntity: ICrudPutAction<IProduct> = entity => async dispatch => {
  const config = {
    headers: {
      'content-type': 'multipart/form-data'
    }
  };
  const formData = formDataFromProduct(entity);
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_PRODUCT,
    payload: axios.post(apiUrl, formData, config)
  });
  dispatch(getEntities());
  return result;
};
export const updateEntity: ICrudPutAction<IProduct> = entity => async dispatch => {
  const config = {
    headers: {
      'content-type': 'multipart/form-data'
    }
  };
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_PRODUCT,
    payload: axios.put(apiUrl, formDataFromProduct(entity), config)
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IProduct> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_PRODUCT,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
