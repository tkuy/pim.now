import { IAttribut } from 'app/shared/model/attribut.model';
import { IValuesList } from 'app/shared/model/values-list.model';

export interface IAttributValuesList {
  id?: number;
  attribut?: IAttribut;
  valuesList?: IValuesList;
}

export const defaultValue: Readonly<IAttributValuesList> = {};
