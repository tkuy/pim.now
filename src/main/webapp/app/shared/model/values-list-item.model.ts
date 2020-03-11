import { IValuesList } from 'app/shared/model/values-list.model';

export interface IValuesListItem {
  id?: number;
  value?: string;
  valuesList?: IValuesList;
}

export const defaultValue: Readonly<IValuesListItem> = {};
