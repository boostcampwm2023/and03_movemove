import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Date } from 'mongoose';

@Schema({ versionKey: false })
export class Action {
  @Prop({ require: true })
  videoId: string;

  @Prop()
  rating: number;

  @Prop()
  reason: string;

  @Prop({
    require: true,
    default: Date.now,
    type: Date,
  })
  updateAt: Date;
}

export const ActionSchema = SchemaFactory.createForClass(Action);
