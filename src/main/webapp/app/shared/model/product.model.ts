import { IFamily } from 'app/shared/model/family.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { ICategory } from 'app/shared/model/category.model';
import { IWorkflow } from 'app/shared/model/workflow.model';
import { IAttributValue } from 'app/shared/model/attribut-value.model';

export interface IProduct {
  id?: number;
  idF?: string;
  nom?: string;
  description?: string;
  family?: IFamily;
  customer?: ICustomer;
  categories?: ICategory[];
  workflows?: IWorkflow[];
  attributValues?: IAttributValue[];
  files?: File[];
}

export const defaultValue: Readonly<IProduct> = {};
