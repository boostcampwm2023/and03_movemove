import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Date, now } from 'mongoose';

@Schema({ versionKey: false })
export class Action {
  @Prop({ require: true })
  videoId: string;

  @Prop()
  rating: number;

  @Prop({
    type: Date,
    require: true,
    default: now(),
  })
  updateAt: Date;
}

export const ActionSchema = SchemaFactory.createForClass(Action);
