import axios from 'axios';
import { IMapping } from 'app/shared/model/mapping.model';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';
import { ICrudGetAction, ICrudPutAction } from 'react-jhipster';
import { IExportProduct } from 'app/shared/model/export.model';

export const ACTION_TYPES = {
  FETCH_MAPPINGS_BY_CUSTOMER: 'import/FETCH_MAPPINGS_BY_CUSTOMER',
  EXPORT_PRODUCT: 'products/EXPORT_PRODUCTS'
};

const initialState = {
  loading: false,
  errorMessage: null,
  updating: false,
  updateSuccess: false,
  mappings: [] as IMapping[],
  exportingProduct: false
};

export type ExportProductState = Readonly<typeof initialState>;

export default (state: ExportProductState = initialState, action): ExportProductState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_MAPPINGS_BY_CUSTOMER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false
      };
    case REQUEST(ACTION_TYPES.EXPORT_PRODUCT):
      return {
        ...state,
        errorMessage: null,
        exportingProduct: true
      };
    case FAILURE(ACTION_TYPES.FETCH_MAPPINGS_BY_CUSTOMER):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case FAILURE(ACTION_TYPES.EXPORT_PRODUCT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_MAPPINGS_BY_CUSTOMER):
      return {
        ...state,
        loading: false,
        mappings: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.EXPORT_PRODUCT):
      return {
        ...state,
        exportingProduct: false
      };
    default:
      return state;
  }
};

const apiUrl = 'api/mappings/customer';
const apiExportProduct = 'api/export';
const TIMEOUT = 20 * 60 * 1000;

export const getMappingsByCustomer: ICrudGetAction<IMapping> = id => {
  const requestUrl = `${apiUrl}`;
  return {
    type: ACTION_TYPES.FETCH_MAPPINGS_BY_CUSTOMER,
    payload: axios.get<IMapping>(requestUrl)
  };
};

export const exportProduct: ICrudPutAction<IExportProduct> = entity => async dispatch => {
  const data = new FormData();
  data.append('idMapping', entity.idMapping);
  data.append('productsIdf', entity.productsIdf);
  const result = await dispatch({
    headers: { 'Content-Type': undefined },
    type: ACTION_TYPES.EXPORT_PRODUCT,
    payload: axios
      .post(apiExportProduct, data, {
        timeout: TIMEOUT,
        headers: {
          'Content-Disposition': 'attachment; filename=template.xlsx',
          'Content-Type': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        },
        responseType: 'arraybuffer'
      })
      .then(response => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', response.headers['content-disposition'].split('filename=')[1]);
        document.body.appendChild(link);
        link.click();
        link.remove();
      })
  });
  dispatch();
  return result;
};
