import { ICategory } from 'app/shared/model/category.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { IProduct } from 'app/shared/model/product.model';

export interface ICategory {
  id?: number;
  idF?: string;
  nom?: string;
  predecessor?: ICategory;
  customer?: ICustomer;
  deleted?: boolean;
  successors?: ICategory[];
  products?: IProduct[];
}

export const defaultValue: Readonly<ICategory> = {};
