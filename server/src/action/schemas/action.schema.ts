import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';

@Schema({ versionKey: false, timestamps: true })
export class Action {
  @Prop({ require: true })
  videoId: string;

  @Prop()
  rating: number;

  @Prop()
  reason: string;

  @Prop()
  seed: number;
}

export const ActionSchema = SchemaFactory.createForClass(Action);
