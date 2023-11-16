import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Date, now } from 'mongoose';

@Schema()
export class Action {
  @Prop({ require: true })
  videoId: string;

  @Prop()
  rating: number;

  @Prop({
    require: true,
    default: now(),
  })
  time: Date;
}

export const ActionSchema = SchemaFactory.createForClass(Action);
