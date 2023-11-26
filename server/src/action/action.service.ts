import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { User } from 'src/decorators/request-user';
import { VideoRatingDTO } from 'src/video/dto/video-rating.dto';
import { Video } from 'src/video/schemas/video.schema';

@Injectable()
export class ActionService {
  constructor(
    @InjectModel('Video') private VideoModel: Model<Video>,
    @InjectModel('User') private UserModel: Model<User>,
  ) {}

  ratingVideo(videoId: string, videoRatingDto: VideoRatingDTO) {
    return `update video rating ${videoId} ${videoRatingDto}`;
  }
}
