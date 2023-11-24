/* eslint-disable no-underscore-dangle */
import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Document, Model } from 'mongoose';
import { Video } from 'src/video/schemas/video.schema';
import { getObject } from 'src/ncpAPI/getObject';
import { User } from './schemas/user.schema';
import { UploadedVideoResponseDto } from './dto/uploaded-video-response.dto';

@Injectable()
export class UserService {
  constructor(
    @InjectModel('Video') private VideoModel: Model<Video>,
    @InjectModel('User') private UserModel: Model<User>,
  ) {}

  getProfile() {
    return 'get profile';
  }

  patchProfile(userDto) {
    return `patch profile ${userDto}`;
  }

  async getUploadedVideos(
    uuid: string,
    limit: number,
    lastId: string,
  ): Promise<UploadedVideoResponseDto> {
    const uploaderData = await this.UserModel.findOne(
      { uuid },
      { __v: 0, actions: 0 },
    );
    // eslint-disable-next-line prettier/prettier
    const { _id: uploaderId, profileImageExtension, ...uploaderInfo } = uploaderData.toObject();
    const profileImage = await this.getBucketImage(
      process.env.PROFILE_BUCKET,
      profileImageExtension,
      uuid,
    );
    const uploader = { ...uploaderInfo, ...(profileImage && { profileImage }) };

    const condition = {
      uploaderId,
      ...(lastId && { _id: { $lt: lastId } }),
    };
    const videoData = await this.VideoModel.find(condition, {
      uploaderId: 0,
      videoExtension: 0,
      __v: 0,
    })
      .sort({ _id: -1 })
      .limit(limit);

    const videos = await this.getVideos(videoData);
    return { videos, uploader };
  }

  async getVideos(videoData: Array<Document>) {
    const videos = await Promise.all(
      videoData.map(async (video) => {
        const { thumbnailExtension, ...videoInfo } = video.toObject();
        const manifest = `${process.env.MANIFEST_URL_PREFIX}${videoInfo._id}_,${process.env.ENCODING_SUFFIXES}${process.env.MANIFEST_URL_SUFFIX}`;
        const thumbnailImage = await this.getBucketImage(
          process.env.THUMBNAIL_BUCKET,
          thumbnailExtension,
          videoInfo._id,
        );
        return { ...videoInfo, manifest, thumbnailImage };
      }),
    );
    return videos;
  }

  async getBucketImage(bucket: string, ImageExtension: string, uuid: string) {
    const bucketImage = ImageExtension
      ? await getObject(bucket, `${uuid}.${ImageExtension}`)
      : null;
    return bucketImage;
  }
}
