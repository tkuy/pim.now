import { IValuesListItem } from 'app/shared/model/values-list-item.model';
import { ICustomer } from 'app/shared/model/customer.model';

export interface IValuesList {
  id?: number;
  idF?: string;
  items?: IValuesListItem[];
  customer?: ICustomer;
}

export const defaultValue: Readonly<IValuesList> = {};
