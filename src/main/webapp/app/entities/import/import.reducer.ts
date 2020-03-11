import axios from 'axios';
import { IMapping } from 'app/shared/model/mapping.model';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';
import { ICrudGetAction, ICrudPutAction } from 'react-jhipster';
import { IImportProduct } from 'app/shared/model/import.model';

export const ACTION_TYPES = {
  FETCH_MAPPINGS_BY_CUSTOMER: 'import/FETCH_MAPPINGS_BY_CUSTOMER',
  IMPORT_PRODUCT: 'import/IMPORT_PRODUCT'
};

const initialState = {
  loading: false,
  errorMessage: null,
  updating: false,
  updateSuccess: false,
  mappings: [] as IMapping[],
  loadingErrorExcel: false,
  importDone: false
};

export type ImportProductState = Readonly<typeof initialState>;

export default (state: ImportProductState = initialState, action): ImportProductState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_MAPPINGS_BY_CUSTOMER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loadingErrorExcel: false
      };
    case REQUEST(ACTION_TYPES.IMPORT_PRODUCT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: false,
        loadingErrorExcel: true
      };
    case FAILURE(ACTION_TYPES.FETCH_MAPPINGS_BY_CUSTOMER):
    case FAILURE(ACTION_TYPES.IMPORT_PRODUCT):
      return {
        ...state,
        loading: false,
        loadingErrorExcel: false,
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
    case SUCCESS(ACTION_TYPES.IMPORT_PRODUCT):
      return {
        ...state,
        loading: false,
        loadingErrorExcel: false,
        importDone: true
      };
    default:
      return state;
  }
};

const apiUrl = 'api/mappings/customer';
const apiUrlImport = 'api/import';
const TIMEOUT = 20 * 60 * 1000;
export const getMappingsByCustomer: ICrudGetAction<IMapping> = id => {
  const requestUrl = `${apiUrl}`;
  return {
    type: ACTION_TYPES.FETCH_MAPPINGS_BY_CUSTOMER,
    payload: axios.get<IMapping>(requestUrl)
  };
};

export const importProductAndGetErrorExcel: ICrudPutAction<IImportProduct> = entity => async dispatch => {
  const data = new FormData();
  data.append('idMapping', entity.idMapping);
  data.append('file', entity.fileToImport);
  const result = await dispatch({
    headers: { 'Content-Type': undefined },
    type: ACTION_TYPES.IMPORT_PRODUCT,
    payload: axios
      .post(apiUrlImport, data, {
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
