import { IMapping } from 'app/shared/model/mapping.model';

export interface IAssociation {
  id?: number;
  column?: string;
  idFAttribut?: string;
  mapping?: IMapping;
}

export const defaultValue: Readonly<IAssociation> = {};
