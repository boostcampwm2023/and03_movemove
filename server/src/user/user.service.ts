/* eslint-disable no-underscore-dangle */
import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Document, Model } from 'mongoose';
import { Video } from 'src/video/schemas/video.schema';
import { UserNotFoundException } from 'src/exceptions/user-not-found.exception';
import * as _ from 'lodash';
import { deleteObject } from 'src/ncpAPI/deleteObject';
import { getBucketImage } from 'src/ncpAPI/getBucketImage';
import { VideoService } from 'src/video/video.service';
import { createPresignedUrl } from 'src/ncpAPI/presignedURL';
import { UploadedVideoResponseDto } from './dto/uploaded-video-response.dto';
import { User } from './schemas/user.schema';
import { ProfileDto } from './dto/profile.dto';
import { RatedVideoResponseDto } from './dto/rated-video-response.dto';
import { ProfileResponseDto } from './dto/profile-response.dto';

@Injectable()
export class UserService {
  constructor(
    @InjectModel('Video') private VideoModel: Model<Video>,
    @InjectModel(User.name) private UserModel: Model<User>,
    private videoService: VideoService,
  ) {}

  async getProfile(userId: string): Promise<ProfileResponseDto> {
    const user = await this.UserModel.findOne({ uuid: userId });

    if (!user) {
      throw new UserNotFoundException();
    }
    const { uuid, profileImageExtension, nickname, statusMessage } = user;
    const profileImageUrl = profileImageExtension
      ? await createPresignedUrl(
          process.env.PROFILE_BUCKET,
          `${uuid}.${profileImageExtension}`,
          'GET',
        )
      : null;
    return {
      nickname,
      statusMessage,
      ...(profileImageUrl && { profileImageUrl }),
    };
  }

  async patchProfile(profileDto: ProfileDto, uuid: string) {
    const updateOption = _.omitBy(profileDto, _.isEmpty); // profileDto 중 빈 문자열인 필드 제거
    const user = await this.UserModel.findOne({ uuid });

    if (user.nickname === updateOption.nickname) {
      // 실제로 변경된 필드만 updateOption에 남기기
      delete updateOption.nickname;
    }
    if (user.statusMessage === updateOption.statusMessage) {
      delete updateOption.statusMessage;
    }
    // TODO profileExtension, profileImageExtension 중 통일하는게 좋으려나
    updateOption.profileImageExtension = updateOption.profileExtension;
    delete updateOption.profileExtension;

    // TODO profileUrl이 null이 아니라면 프로필 이미지가 업로드됐는지 확인
    const profileUrl = profileDto.profileExtension
      ? await createPresignedUrl(
          process.env.PROFILE_BUCKET,
          `${uuid}.${profileDto.profileExtension}`,
          'GET',
        )
      : null;
    if (profileDto.profileExtension === '' && user.profileImageExtension) {
      // profileExtension 필드를 빈 문자열로 주었고, 기존 프로필이미지가 있었다면 삭제
      deleteObject(
        process.env.PROFILE_BUCKET,
        `${uuid}.${user.profileImageExtension}`,
      );
      updateOption.profileImageExtension = null;
    }

    // TODO findOneAndUpdate이 아니라 다른걸 써서 update 결과를 받고 싶음
    const result = (
      await this.UserModel.findOneAndUpdate({ uuid }, updateOption)
    ).toObject();
    return { ...result, profileUrl };
  }

  async getUploadedVideos(
    uuid: string,
    limit: number,
    lastId: string,
  ): Promise<UploadedVideoResponseDto> {
    const uploaderData = await this.UserModel.findOne({ uuid }, { actions: 0 });
    const { uploader, uploaderId } = await this.getUploaderInfo(
      uuid,
      uploaderData.toObject(),
    );
    const condition = {
      uploaderId,
      ...(lastId && { _id: { $lt: lastId } }),
    };
    const videoData = await this.VideoModel.find(
      condition,
      {
        uploaderId: 0,
        videoExtension: 0,
      },
      { lean: true },
    )
      .sort({ _id: -1 })
      .limit(limit);

    const videos = await this.getVideoInfos(videoData);
    return { videos, uploader };
  }

  async getUploaderInfo(uuid: string, uploaderData) {
    const {
      _id: uploaderId,
      profileImageExtension,
      ...uploaderInfo
    } = uploaderData;
    const profileImageUrl = profileImageExtension
      ? await createPresignedUrl(
          process.env.PROFILE_BUCKET,
          `${uuid}.${profileImageExtension}`,
          'GET',
        )
      : null;
    const uploader = {
      ...uploaderInfo,
      ...(profileImageUrl && { profileImageUrl }),
    };
    return { uploader, uploaderId };
  }

  async getVideoInfos(videoData: Array<any>) {
    const videos = await Promise.all(
      videoData.map(async (video) => {
        const { thumbnailExtension, raterCount, totalRating, ...videoInfo } =
          video;
        const rating = raterCount
          ? (totalRating / raterCount).toFixed(1)
          : null;
        const manifest = `${process.env.SERVER_URL}videos/${videoInfo._id}/manifest`;
        const thumbnailImage = await getBucketImage(
          process.env.THUMBNAIL_BUCKET,
          thumbnailExtension,
          videoInfo._id,
        );
        return { ...videoInfo, manifest, rating, thumbnailImage };
      }),
    );
    return videos;
  }

  async getRatedVideos(
    uuid: string,
    limit: number,
    lastRatedAt: string,
  ): Promise<RatedVideoResponseDto> {
    const array = lastRatedAt
      ? {
          $filter: {
            input: '$actions',
            as: 'action',
            cond: { $lt: ['$$action.updatedAt', new Date(lastRatedAt)] },
          },
        }
      : '$actions';

    const data = await this.UserModel.aggregate([
      { $match: { uuid } },
      {
        $project: {
          _id: 0,
          uuid: 1,
          nickname: 1,
          actions: {
            $slice: [
              {
                $sortArray: {
                  input: array,
                  sortBy: { updatedAt: -1 },
                },
              },
              limit,
            ],
          },
        },
      },
    ]);

    if (!data.length) throw new UserNotFoundException();

    const { actions, ...rater } = data.pop();
    const videos = await Promise.all(
      actions.map(async (action) => {
        const videoData = await this.VideoModel.findOne({
          _id: action.videoId,
        }).populate('uploaderId', '-_id -actions');
        const video = await this.videoService.getVideoInfo(
          videoData.toObject(),
        );
        return { ...video, ratedAt: action.updatedAt };
      }),
    );

    return { rater, videos };
  }
}
