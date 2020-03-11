import { ICustomer } from 'app/shared/model/customer.model';

export interface IConfigurationCustomer {
  id?: number;
  urlPrestashop?: string;
  apiKeyPrestashop?: string;
  customer?: ICustomer;
}

export const defaultValue: Readonly<IConfigurationCustomer> = {};
