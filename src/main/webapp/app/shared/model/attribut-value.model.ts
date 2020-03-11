import { IAttribut } from 'app/shared/model/attribut.model';
import { IProduct } from 'app/shared/model/product.model';

export interface IAttributValue {
  id?: number;
  value?: string;
  attribut?: IAttribut;
  product?: IProduct;
  // Only used when there is a MultipleValue
  values?: string[];
  currentValue?: string;
}

export const defaultValue: Readonly<IAttributValue> = {};
