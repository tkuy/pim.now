export interface IExportProduct {
  idMapping?: any;
  productsIdf?: any;
}

export const defaultValue: Readonly<IExportProduct> = {
  idMapping: null,
  productsIdf: null
};
