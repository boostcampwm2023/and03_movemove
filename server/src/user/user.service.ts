/* eslint-disable no-underscore-dangle */
import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Document, Model } from 'mongoose';
import { Video } from 'src/video/schemas/video.schema';
import { putObject } from 'src/ncpAPI/putObject';
import { UserNotFoundException } from 'src/exceptions/user-not-found.exception';
import * as _ from 'lodash';
import { deleteObject } from 'src/ncpAPI/deleteObject';
import { getBucketImage } from 'src/ncpAPI/getBucketImage';
import { VideoService } from 'src/video/video.service';
import { UploadedVideoResponseDto } from './dto/uploaded-video-response.dto';
import { User } from './schemas/user.schema';
import { ProfileDto } from './dto/profile.dto';

@Injectable()
export class UserService {
  constructor(
    @InjectModel('Video') private VideoModel: Model<Video>,
    @InjectModel(User.name) private UserModel: Model<User>,
    private videoService: VideoService,
  ) {}

  async getProfile(userId: string) {
    const user = await this.UserModel.findOne({ uuid: userId });

    if (!user) {
      throw new UserNotFoundException();
    }
    const { uuid, profileImageExtension, nickname, statusMessage } = user;
    const profileImage = await getBucketImage(
      process.env.PROFILE_BUCKET,
      profileImageExtension,
      uuid,
    );
    return new ProfileDto({
      nickname,
      statusMessage,
      ...(profileImage && { profileImage }),
    });
  }

  async patchProfile(
    profileDto: ProfileDto,
    profileImage: Express.Multer.File,
    uuid: string,
  ): Promise<ProfileDto> {
    const updateOption = _.omitBy(profileDto, _.isEmpty); // profileDto 중 빈 문자열인 필드 제거
    const user = await this.UserModel.findOne({ uuid });
    if (user.nickname === updateOption.nickname) {
      // 실제로 변경된 필드만 updateOption에 남기기
      delete updateOption.nickname;
    }
    if (user.statusMessage === updateOption.statusMessage) {
      delete updateOption.statusMessage;
    }

    let updatedProfileImage;
    if (profileImage) {
      const profileImageExtension = profileImage.originalname.split('.').pop();
      putObject(
        process.env.PROFILE_BUCKET,
        `${uuid}.${profileImageExtension}`,
        profileImage.buffer,
      );
      updateOption.profileImageExtension = profileImageExtension;
      updatedProfileImage = profileImage.buffer;
    } else if (
      'profileImage' in profileDto &&
      user.profileImageExtension !== null
    ) {
      // profileImage 필드를 빈 문자열로 주었고, 기존 프로필이미지가 있었다면 삭제
      deleteObject(
        process.env.PROFILE_BUCKET,
        `${uuid}.${user.profileImageExtension}`,
      );
      updateOption.profileImageExtension = null;
      updatedProfileImage = null;
    }

    await this.UserModel.updateOne({ uuid }, updateOption);
    if (updatedProfileImage !== undefined) {
      updateOption.profileImage = updatedProfileImage;
      delete updateOption.profileImageExtension;
    }
    return updateOption;
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
    const videoData = await this.VideoModel.find(condition, {
      uploaderId: 0,
      videoExtension: 0,
    })
      .sort({ _id: -1 })
      .limit(limit);

    const videos = await this.getVideoInfos(videoData);
    return { videos, uploader };
  }

  async getUploaderInfo(uuid: string, uploaderData) {
    // eslint-disable-next-line prettier/prettier
    const { _id: uploaderId, profileImageExtension, ...uploaderInfo } = uploaderData;
    const profileImage = await getBucketImage(
      process.env.PROFILE_BUCKET,
      profileImageExtension,
      uuid,
    );
    const uploader = { ...uploaderInfo, ...(profileImage && { profileImage }) };
    return { uploader, uploaderId };
  }

  async getVideoInfos(videoData: Array<Document>) {
    const videos = await Promise.all(
      videoData.map(async (video) => {
        const { thumbnailExtension, ...videoInfo } = video.toObject();
        const manifest = `${process.env.MANIFEST_URL_PREFIX}${videoInfo._id}_,${process.env.ENCODING_SUFFIXES}${process.env.MANIFEST_URL_SUFFIX}`;
        const thumbnailImage = await getBucketImage(
          process.env.THUMBNAIL_BUCKET,
          thumbnailExtension,
          videoInfo._id,
        );
        return { ...videoInfo, manifest, thumbnailImage };
      }),
    );
    return videos;
  }

  async getRatedVideos(uuid: string, limit: number, lastRatedAt: string) {
    const array = lastRatedAt
      ? {
          $filter: {
            input: '$actions',
            as: 'action',
            cond: { $lte: ['$$action.updatedAt', new Date(lastRatedAt)] },
          },
        }
      : '$actions';
    const data = await this.UserModel.aggregate([
      { $match: { uuid } },
      {
        $project: {
          uuid: 1,
          nickname: 1,
          actions: {
            $slice: [array, limit],
          },
        },
      },
    ]);
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
