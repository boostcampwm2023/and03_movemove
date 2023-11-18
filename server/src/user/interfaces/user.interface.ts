import { Document } from 'mongoose';
import { Action } from 'src/action/schemas/action.schema';

export interface User extends Document {
  readonly uuid: string;
  readonly nickname: string;
  readonly actions: Action[];
  readonly statusMessage: string;
}
