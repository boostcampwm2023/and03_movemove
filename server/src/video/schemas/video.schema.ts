import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import mongoose, { Date, HydratedDocument } from 'mongoose';
import { User } from 'src/user/schemas/user.schema';

export type VideoDocument = HydratedDocument<Video>;

@Schema({
  versionKey: false,
  timestamps: { createdAt: 'uploadedAt', updatedAt: false },
})
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
  viewCount: number; // 조회수

  @Prop({
    require: true,
  })
  thumbnailExtension: string; // 썸네일 확장자

  @Prop({
    require: true,
  })
  videoExtension: string; // 비디오 확장자

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
