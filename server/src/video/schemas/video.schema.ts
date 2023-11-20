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
  uploaderId: User;

  @Prop({
    require: true,
    default: 0,
  })
  viewCount: string; // 조회수

  @Prop({
    require: true,
    default: 0,
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
