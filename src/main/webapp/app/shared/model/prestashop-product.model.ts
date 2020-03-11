import { IProduct } from 'app/shared/model/product.model';

export interface IPrestashopProduct {
  id?: number;
  prestashopProductId?: number;
  productPim?: IProduct;
}

export const defaultValue: Readonly<IPrestashopProduct> = {};
