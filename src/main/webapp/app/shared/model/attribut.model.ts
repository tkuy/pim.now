import { ICustomer } from 'app/shared/model/customer.model';
import { IFamily } from 'app/shared/model/family.model';
import { AttributType } from 'app/shared/model/enumerations/attribut-type.model';

export interface IAttribut {
  id?: number;
  idF?: string;
  nom?: string;
  type?: AttributType;
  customer?: ICustomer;
  families?: IFamily[];
}

export const defaultValue: Readonly<IAttribut> = {};
