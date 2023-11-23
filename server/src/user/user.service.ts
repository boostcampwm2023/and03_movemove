/* eslint-disable no-underscore-dangle */
import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { Video } from 'src/video/schemas/video.schema';
import { User } from './schemas/user.schema';

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

  async getUploadedVideos(uuid: string, limit: number, lastId: string) {
    const uploaderId = await this.UserModel.findOne({ uuid });
    const condition = lastId
      ? { uploaderId, _id: { $lt: lastId } }
      : { uploaderId };

    const videoData = await this.VideoModel.find(condition, {
      uploaderId: 0,
      thumbnailExtension: 0,
      videoExtension: 0,
      __v: 0,
    })
      .sort({ _id: -1 })
      .limit(limit);

    const videos = videoData.map((video) => {
      const manifest = `${process.env.MANIFEST_URL_PREFIX}${video._id}_,${process.env.ENCODING_SUFFIXES}${process.env.MANIFEST_URL_SUFFIX}`;
      return { ...video.toObject(), manifest };
    });
    return { videos };
  }
}
