/* eslint-disable no-underscore-dangle */
import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { Video } from 'src/video/schemas/video.schema';
import { UserNotFoundException } from 'src/exceptions/user-not-found.exception';
import * as _ from 'lodash';
import { deleteObject } from 'src/ncpAPI/deleteObject';
import { VideoService } from 'src/video/video.service';
import { createPresignedUrl } from 'src/ncpAPI/presignedURL';
import { ProfileUploadRequiredException } from 'src/exceptions/profile-upload-required-exception';
import { checkUpload } from 'src/ncpAPI/listObjects';
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
    const updateOption: ProfileDto = _.omitBy(profileDto, _.isEmpty); // profileDto 중 빈 문자열인 필드 제거
    const user = await this.UserModel.findOne({ uuid }, {}, { lean: true });

    // profileExtension이 null이 아니라면 프로필 이미지가 업로드됐는지 확인
    if (
      profileDto.profileImageExtension &&
      !(await checkUpload(
        process.env.PROFILE_BUCKET,
        `${uuid}.${profileDto.profileImageExtension}`,
      ))
    ) {
      throw new ProfileUploadRequiredException();
    }

    if (profileDto.profileImageExtension === '' && user.profileImageExtension) {
      // profileExtension 필드를 빈 문자열로 주었고, 기존 프로필이미지가 있었다면 삭제
      deleteObject(
        process.env.PROFILE_BUCKET,
        `${uuid}.${user.profileImageExtension}`,
      );
      updateOption.profileImageExtension = null;
    }

    const result = await this.UserModel.findOneAndUpdate(
      { uuid },
      updateOption,
      {
        lean: true,
        new: true,
      },
    );

    const profileImageUrl = result.profileImageExtension
      ? await createPresignedUrl(
          process.env.PROFILE_BUCKET,
          `${uuid}.${result.profileImageExtension}`,
          'GET',
        )
      : null;
    return {
      nickname: result.nickname,
      statusMessage: result.statusMessage,
      profileImageUrl,
    };
  }

  async getUploadedVideos(uuid: string, limit: number, lastId: string) {
    const uploaderData = await this.UserModel.findOne({ uuid }, { actions: 0 });
    if (!uploaderData) {
      throw new UserNotFoundException();
    }

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

    const videos = await this.getVideoInfos(videoData, uploader);
    return { videos };
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

  async getVideoInfos(videoData: Array<any>, uploader: object) {
    const videos = await Promise.all(
      videoData.map(async (video) => {
        const { thumbnailExtension, raterCount, totalRating, ...videoInfo } =
          video;
        const rating = raterCount
          ? (totalRating / raterCount).toFixed(1)
          : null;
        const manifest = `${process.env.MANIFEST_URL_PREFIX}/${videoInfo._id}_master.m3u8`;
        const thumbnailImageUrl = await createPresignedUrl(
          process.env.THUMBNAIL_BUCKET,
          `${videoInfo._id}.${thumbnailExtension}`,
          'GET',
        );
        return {
          video: { ...videoInfo, manifest, rating, thumbnailImageUrl },
          uploader,
        };
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
        const video = videoData
          ? await this.videoService.getVideoInfo(videoData.toObject(), uuid)
          : { video: null, uploader: null };
        return { ...video, ratedAt: action.updatedAt };
      }),
    );

    return { rater, videos };
  }
}
