import axios from 'axios';
import { ICrudGetAction } from 'react-jhipster';

import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

export const ACTION_TYPES = {
  FETCH_DASHBOARD: 'dashboard/FETCH_DASHBOARD',
  RESET: 'dashboard/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entity: {},
  updating: false,
  updateSuccess: false
};

export type DashboardState = Readonly<typeof initialState>;

export default (state: DashboardState = initialState, action): DashboardState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_DASHBOARD):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_DASHBOARD):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_DASHBOARD):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/dashboard';

export const getEntity: ICrudGetAction<{}> = id => {
  const requestUrl = `${apiUrl}`;
  return {
    type: ACTION_TYPES.FETCH_DASHBOARD,
    payload: axios.get<{}>(requestUrl)
  };
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
