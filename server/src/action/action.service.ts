import { Injectable } from '@nestjs/common';
import { InjectConnection, InjectModel } from '@nestjs/mongoose';
import { Model, Connection } from 'mongoose';
import { User } from 'src/decorators/request-user';
import { NeverViewVideoException } from 'src/exceptions/never-view-video.exception';
import { ReasonRequiredException } from 'src/exceptions/reason-required.exception';
import { VideoNotFoundException } from 'src/exceptions/video-not-found.exception';
import { VideoRatingDTO } from 'src/video/dto/video-rating.dto';
import { Video } from 'src/video/schemas/video.schema';

@Injectable()
export class ActionService {
  constructor(
    @InjectConnection() private readonly connection: Connection,
    @InjectModel('Video') private VideoModel: Model<Video>,
    @InjectModel('User') private UserModel: Model<User>,
  ) {}

  async viewVideo(videoId: string, userId: string, seed: number) {
    // Using custom connection
    const session = await this.connection.startSession();
    await session.withTransaction(async () => {
      await this.VideoModel.updateOne(
        { _id: videoId },
        { $inc: { viewCount: 1 } },
      )
        .session(session)
        .then((result) => {
          if (result.modifiedCount === 0) {
            throw new VideoNotFoundException();
          }
        });

      // 기존에 시청한적이 있다면 seed만 업데이트
      const modifiedCount = await this.UserModel.updateOne(
        {
          uuid: userId,
          'actions.videoId': videoId,
        },
        { $set: { 'actions.$.seed': seed ?? null } },
      )
        .session(session)
        .then((result) => result.modifiedCount);

      if (modifiedCount === 0) {
        // 처음 시청한다면 action document를 push
        await this.UserModel.updateOne(
          {
            uuid: userId,
          },
          {
            $push: {
              actions: {
                videoId,
                seed: seed ?? null,
                reason: null,
                rating: null,
              },
            },
          },
        ).session(session);
      }
    });
    session.endSession();
  }

  async ratingVideo(
    videoId: string,
    videoRatingDto: VideoRatingDTO,
    userId: string,
  ) {
    if (videoRatingDto.rating <= 2 && !videoRatingDto.reason) {
      throw new ReasonRequiredException();
    }
    // userId의 action을 조회해서 이전에 준 별점을 확인
    const user: any = await this.UserModel.findOne(
      { uuid: userId },
      { actions: { $elemMatch: { videoId } } },
    );
    if (user.actions.length === 0) throw new NeverViewVideoException();
    // 2점 이하
    const condition = {
      'actions.$[elem].rating': videoRatingDto.rating,
      'actions.$[elem].reason': videoRatingDto.reason,
    };
    const prevRating = user.actions.pop().rating;
    const ratingDelta = videoRatingDto.rating - prevRating ?? 0;
    const raterCountDelta = prevRating ? 0 : 1;

    const session = await this.connection.startSession();
    await session.withTransaction(async () => {
      await this.UserModel.updateOne(
        { uuid: userId },
        { $set: condition },
        { arrayFilters: [{ 'elem.videoId': videoId }] },
      ).session(session);

      if (raterCountDelta || ratingDelta) {
        await this.VideoModel.updateOne(
          { _id: videoId },
          { $inc: { raterCount: raterCountDelta, totalRating: ratingDelta } },
        )
          .session(session)
          .then((result) => {
            if (result.modifiedCount === 0) {
              throw new VideoNotFoundException();
            }
          });
      }
    });

    session.endSession();
    return { videoId, ...videoRatingDto };
  }
}
