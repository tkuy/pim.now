import { ICustomer } from 'app/shared/model/customer.model';
import { IUser } from 'app/shared/model/user.model';

export interface IUserWithUserExtra {
  user?: IUser;
  phone?: string;
  customer?: ICustomer;
}

export const defaultValue: Readonly<IUserWithUserExtra> = {};
