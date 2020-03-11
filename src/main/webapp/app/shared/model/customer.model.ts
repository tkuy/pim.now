import { IConfigurationCustomer } from 'app/shared/model/configuration-customer.model';

export interface ICustomer {
  id?: number;
  idF?: string;
  name?: string;
  description?: string;
  familyRoot?: number;
  categoryRoot?: number;
  isDeleted?: boolean;
  configuration?: IConfigurationCustomer;
}

export const defaultValue: Readonly<ICustomer> = {
  isDeleted: false
};
