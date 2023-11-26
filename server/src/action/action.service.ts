import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { User } from 'src/decorators/request-user';
import { VideoNotFoundException } from 'src/exceptions/video-not-found.exception';
import { VideoRatingDTO } from 'src/video/dto/video-rating.dto';
import { Video } from 'src/video/schemas/video.schema';

@Injectable()
export class ActionService {
  constructor(
    @InjectModel('Video') private VideoModel: Model<Video>,
    @InjectModel('User') private UserModel: Model<User>,
  ) {}

  async ratingVideo(
    videoId: string,
    videoRatingDto: VideoRatingDTO,
    userId: string,
  ) {
    const video = await this.VideoModel.findOne({ _id: videoId });

    if (!video) {
      throw new VideoNotFoundException();
    }
    // userId의 action을 조회해서 videoId로 별점준적이 있는지 확인
    const user: any = await this.UserModel.findOne(
      {
        uuid: userId,
      },
      {
        actions: { $elemMatch: { videoId } },
      },
    );
    if (user.actions.length === 0) {
      // 조회하지 않은 영상입니다 throw
    }
    const prevRating = user.actions[0].rating;
    const ratingDelta = videoRatingDto.rating - prevRating ?? 0;
    // 별점 준적 없으면 raterCount 늘리고
    // 트랜잭션 시작
    const raterCountDelta = prevRating ? 0 : 1;

    this.VideoModel.updateOne(
      { _id: videoId },
      { $inc: { raterCount: raterCountDelta, totalRating: ratingDelta } },
      (err, result) => {
        if (err) console.log(err);
        else console.log(result);
      },
    );
    // totalRating 늘리고 (별점 준적이 있으면 그 차만)

    // 트랜잭션 끝
    return `update video rating ${videoId} ${videoRatingDto}`;
  }
}
