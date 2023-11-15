import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import mongoose from 'mongoose';
import { User } from 'src/user/schemas/user.schema';
import { Video } from 'src/video/schemas/video.schema';

@Schema()
export class SubDocument {
  @Prop({ type: mongoose.Schema.Types.ObjectId, ref: 'Video' })
  videoId: Video;

  @Prop()
  rating: number;
}

const subDocumentSchema = SchemaFactory.createForClass(SubDocument);

@Schema()
export class Action {
  @Prop({ type: mongoose.Schema.Types.ObjectId, ref: 'User' })
  userId: User;

  @Prop({ type: [{ type: subDocumentSchema }] })
  subDocument: SubDocument[];
}

export const ActionSchema = SchemaFactory.createForClass(Action);
