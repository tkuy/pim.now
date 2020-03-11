export interface IImportProduct {
  idMapping?: any;
  fileToImport?: any;
}

export const defaultValue: Readonly<IImportProduct> = {
  idMapping: null,
  fileToImport: null
};
