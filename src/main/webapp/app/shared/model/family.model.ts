import { IFamily } from 'app/shared/model/family.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { IAttribut } from 'app/shared/model/attribut.model';

export interface IFamily {
  id?: number;
  idF?: string;
  nom?: string;
  family?: IFamily;
  successors?: IFamily[];
  deleted?: boolean;
  idPredecessor?: number;
  predecessor?: IFamily;
  customer?: ICustomer;
  // Existing attributes
  attributes?: IAttribut[];
  newExistingAttributes?: IAttribut[];
  // Attributes to create
  newAttributes?: IAttribut[];
}

export const defaultValue: Readonly<IFamily> = {};
