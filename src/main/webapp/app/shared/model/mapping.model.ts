import { ICustomer } from 'app/shared/model/customer.model';
import { IAssociation } from 'app/shared/model/association.model';

export interface IMapping {
  id?: number;
  idF?: string;
  name?: string;
  description?: string;
  separator?: string;
  customer?: ICustomer;
  associations?: IAssociation[];
}

export const defaultValue: Readonly<IMapping> = {};

interface IMappingDuplicateAssociation {
  column?: string;
  idAttribut?: number;
  nameAttribut?: string;
}

export interface IMappingDuplicate {
  mapping?: IMapping;
  associations?: IMappingDuplicateAssociation[];
}

export const defaultValueDup: Readonly<IMappingDuplicate> = {};
