/* eslint-disable @typescript-eslint/naming-convention */
/* eslint-disable no-underscore-dangle */
/* eslint-disable class-methods-use-this */
import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { putObject } from 'src/ncpAPI/putObject';
import { requestEncoding } from 'src/ncpAPI/requestEncoding';
import { User } from 'src/user/schemas/user.schema';
import { getObject } from 'src/ncpAPI/getObject';
import { VideoDto } from './dto/video.dto';
import { VideoRatingDTO } from './dto/video-rating.dto';
import { Video } from './schemas/video.schema';

@Injectable()
export class VideoService {
  constructor(
    @InjectModel('Video') private VideoModel: Model<Video>,
    @InjectModel('User') private UserModel: Model<User>,
  ) {}

  async getRandomVideo(category: string, limit: number) {
    const videos = await this.VideoModel.aggregate([
      { $match: { category } },
      { $sample: { size: limit } },
      { $project: { __v: 0 } },
    ]);

    await this.UserModel.populate(videos, {
      path: 'uploaderId',
      select: '-_id -actions -__v',
    });

    const videoData = await Promise.all(
      videos.map(async (video) => {
        const { totalRating, raterCount, uploaderId, ...videoInfo } = video;
        const rating = totalRating / raterCount.toFixed(1);

        const { profileImageExtension, uuid, ...uploaderInfo } =
          uploaderId._doc;
        const profileImage = await getObject(
          process.env.PROFILE_BUCKET,
          `${uuid}.${profileImageExtension}`,
        );

        return {
          video: { ...videoInfo, rating },
          uploader: { ...uploaderInfo, profileImage, uuid },
        };
      }),
    );
    return videoData;
  }

  updateVideoRating(videoId: string, videoRatingDto: VideoRatingDTO) {
    return `update video rating ${videoId} ${videoRatingDto}`;
  }

  setVideoRating(videoId: string, videoRatingDto: VideoRatingDTO) {
    return `set video rating ${videoId} ${videoRatingDto}`;
  }

  async uploadVideo(files: any, videoDto: VideoDto, uuid: string) {
    const { title, content, category } = videoDto;
    const video = files.video.pop();
    const thumbnail = files.thumbnail.pop();

    const uploader = await this.UserModel.findOne({ uuid });
    const newVideo = new this.VideoModel({
      title,
      content,
      category,
      uploaderId: uploader._id,
    });

    const videoExtension = video.originalname.split('.').pop();
    const thumbnailExtension = thumbnail.originalname.split('.').pop();

    const videoName = `${newVideo._id}.${videoExtension}`;
    const thumbnailName = `${newVideo._id}.${thumbnailExtension}`;

    await Promise.all([
      newVideo.save(),
      putObject(process.env.INPUT_BUCKET, videoName, video.buffer),
      putObject(process.env.THUMBNAIL_BUCKET, thumbnailName, thumbnail.buffer),
    ]);
    await requestEncoding(process.env.INPUT_BUCKET, [videoName]);

    return { video: videoDto };
  }

  deleteVideo(videoId: string) {
    return `delete video ${videoId}`;
  }

  getTrendVideo(limit: number) {
    return `get trend video ${limit}`;
  }

  getTopRatedVideo(category: string) {
    return `get top rated video ${category}`;
  }

  getVideo(videoId: string) {
    return `get video ${videoId}`;
  }
}
