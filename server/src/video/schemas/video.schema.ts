import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import mongoose, { Date, HydratedDocument, now } from 'mongoose';
import { User } from 'src/user/schemas/user.schema';

export type VideoDocument = HydratedDocument<Video>;

@Schema()
export class Video {
  @Prop({
    require: true,
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
  })
  uploaderId: mongoose.Schema.Types.ObjectId;

  @Prop({
    require: true,
    default: 0,
  })
  viewCount: number; // 조회수

  @Prop({
    require: true,
    default: 0, // default를 0으로 하는게 맞을까?
  })
  totalRating: number; // 총별점

  @Prop({
    require: true,
    default: 0,
  })
  raterCount: number; // 별점준사람 수

  @Prop({
    require: true,
    default: now(),
    type: Date,
  })
  uploadTime: Date; // 업로드시간

  @Prop({
    require: true,
  })
  category: string;

  @Prop({
    require: true,
  })
  title: string;

  @Prop({
    require: true,
  })
  content: string;
}

export const VideoSchema = SchemaFactory.createForClass(Video);
