import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

export type UserDocument = HydratedDocument<User>;

@Schema()
export class User {
  @Prop({
    require: true,
    unique: true,
  })
  uuid: string;

  @Prop({
    require: true,
  })
  nickname: string;

  @Prop({
    require: true,
  })
  profileImageURL: string;

  @Prop({
    require: true,
  })
  statusMessage: string;
}

export const UserSchema = SchemaFactory.createForClass(User);
