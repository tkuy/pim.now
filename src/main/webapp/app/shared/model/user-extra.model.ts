import { ICustomer } from 'app/shared/model/customer.model';
import { IUser } from 'app/shared/model/user.model';

export interface IUserExtra {
  id?: number;
  phone?: string;
  customer?: ICustomer;
  user?: IUser;
}

export const defaultValue: Readonly<IUserExtra> = {};
