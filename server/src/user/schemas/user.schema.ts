import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';
import { Action, ActionSchema } from 'src/action/schemas/action.schema';

export type UserDocument = HydratedDocument<User>;

@Schema()
export class User {
  @Prop({
    require: true,
    unique: true,
  })
  uuid: string;

  @Prop({ type: [{ type: ActionSchema }] })
  actions: Action[];

  @Prop({ require: true })
  nickname: string;

  @Prop({ require: true })
  statusMessage: string;

  @Prop()
  profileImageExtension: string;
}

export const UserSchema = SchemaFactory.createForClass(User);
