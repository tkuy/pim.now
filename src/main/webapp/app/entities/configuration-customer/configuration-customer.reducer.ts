import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudSearchAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, IConfigurationCustomer } from 'app/shared/model/configuration-customer.model';

export const ACTION_TYPES = {
  SEARCH_CONFIGURATIONCUSTOMERS: 'configurationCustomer/SEARCH_CONFIGURATIONCUSTOMERS',
  FETCH_CONFIGURATIONCUSTOMER_LIST: 'configurationCustomer/FETCH_CONFIGURATIONCUSTOMER_LIST',
  FETCH_CONFIGURATIONCUSTOMER: 'configurationCustomer/FETCH_CONFIGURATIONCUSTOMER',
  CREATE_CONFIGURATIONCUSTOMER: 'configurationCustomer/CREATE_CONFIGURATIONCUSTOMER',
  UPDATE_CONFIGURATIONCUSTOMER: 'configurationCustomer/UPDATE_CONFIGURATIONCUSTOMER',
  DELETE_CONFIGURATIONCUSTOMER: 'configurationCustomer/DELETE_CONFIGURATIONCUSTOMER',
  RESET: 'configurationCustomer/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IConfigurationCustomer>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type ConfigurationCustomerState = Readonly<typeof initialState>;

// Reducer

export default (state: ConfigurationCustomerState = initialState, action): ConfigurationCustomerState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_CONFIGURATIONCUSTOMERS):
    case REQUEST(ACTION_TYPES.FETCH_CONFIGURATIONCUSTOMER_LIST):
    case REQUEST(ACTION_TYPES.FETCH_CONFIGURATIONCUSTOMER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_CONFIGURATIONCUSTOMER):
    case REQUEST(ACTION_TYPES.UPDATE_CONFIGURATIONCUSTOMER):
    case REQUEST(ACTION_TYPES.DELETE_CONFIGURATIONCUSTOMER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_CONFIGURATIONCUSTOMERS):
    case FAILURE(ACTION_TYPES.FETCH_CONFIGURATIONCUSTOMER_LIST):
    case FAILURE(ACTION_TYPES.FETCH_CONFIGURATIONCUSTOMER):
    case FAILURE(ACTION_TYPES.CREATE_CONFIGURATIONCUSTOMER):
    case FAILURE(ACTION_TYPES.UPDATE_CONFIGURATIONCUSTOMER):
    case FAILURE(ACTION_TYPES.DELETE_CONFIGURATIONCUSTOMER):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_CONFIGURATIONCUSTOMERS):
    case SUCCESS(ACTION_TYPES.FETCH_CONFIGURATIONCUSTOMER_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_CONFIGURATIONCUSTOMER):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_CONFIGURATIONCUSTOMER):
    case SUCCESS(ACTION_TYPES.UPDATE_CONFIGURATIONCUSTOMER):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_CONFIGURATIONCUSTOMER):
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

const apiUrl = 'api/configuration-customers';
const apiSearchUrl = 'api/_search/configuration-customers';

// Actions

export const getSearchEntities: ICrudSearchAction<IConfigurationCustomer> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_CONFIGURATIONCUSTOMERS,
  payload: axios.get<IConfigurationCustomer>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<IConfigurationCustomer> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_CONFIGURATIONCUSTOMER_LIST,
  payload: axios.get<IConfigurationCustomer>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IConfigurationCustomer> = () => {
  const requestUrl = `${apiUrl}`;
  return {
    type: ACTION_TYPES.FETCH_CONFIGURATIONCUSTOMER,
    payload: axios.get<IConfigurationCustomer>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IConfigurationCustomer> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_CONFIGURATIONCUSTOMER,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IConfigurationCustomer> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_CONFIGURATIONCUSTOMER,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IConfigurationCustomer> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_CONFIGURATIONCUSTOMER,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
