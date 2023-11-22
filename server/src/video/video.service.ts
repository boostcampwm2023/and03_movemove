/* eslint-disable no-underscore-dangle */
/* eslint-disable class-methods-use-this */
import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { putObject } from 'src/ncpAPI/putObject';
import { requestEncoding } from 'src/ncpAPI/requestEncoding';
import { User } from 'src/user/schemas/user.schema';
import { deleteObject } from 'src/ncpAPI/deleteObject';
import { VideoNotFoundException } from 'src/exceptions/video-not-found.exception';
import { NotYourVideoException } from 'src/exceptions/not-your-video.exception';
import { VideoDto } from './dto/video.dto';
import { VideoRatingDTO } from './dto/video-rating.dto';
import { Video } from './schemas/video.schema';

@Injectable()
export class VideoService {
  constructor(
    @InjectModel('Video') private VideoModel: Model<Video>,
    @InjectModel('User') private UserModel: Model<User>,
  ) {}

  getRandomVideo(category: string, limit: number) {
    return `get random video ${category} ${limit}`;
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

    const videoExtension = video.originalname.split('.').pop();
    const thumbnailExtension = thumbnail.originalname.split('.').pop();

    const newVideo = new this.VideoModel({
      title,
      content,
      category,
      uploaderId: uploader._id,
      thumbnailExtension,
      videoExtension,
    });

    const videoName = `${newVideo._id}.${videoExtension}`;
    const thumbnailName = `${newVideo._id}.${thumbnailExtension}`;

    await Promise.all([
      newVideo.save(),
      putObject(process.env.INPUT_BUCKET, videoName, video.buffer),
      putObject(process.env.THUMBNAIL_BUCKET, thumbnailName, thumbnail.buffer),
    ]);
    await requestEncoding(process.env.INPUT_BUCKET, [videoName]);

    return { video: videoDto, videoId: newVideo._id };
  }

  async deleteEncodedVideo(videoId: string) {
    const encodingSuffixes = process.env.ENCODING_SUFFIXES.split(' ');
    const fileNamePrefix = `${process.env.VIDEO_OUTPUT_PATH}/${videoId}`;
    return Promise.all([
      ...encodingSuffixes.map((suffix) =>
        deleteObject(
          process.env.OUTPUT_BUCKET,
          `${fileNamePrefix}_${suffix}.mp4`,
        ),
      ),
    ]);
  }

  async deleteVideo(videoId: string, uuid: string) {
    const video = await this.VideoModel.findOne({ _id: videoId }).populate(
      'uploaderId',
    );
    if (!video) {
      throw new VideoNotFoundException();
    }
    if (video.uploaderId.uuid !== uuid) {
      throw new NotYourVideoException();
    }
    await Promise.all([
      deleteObject(
        process.env.INPUT_BUCKET,
        `${videoId}.${video.videoExtension}`,
      ),
      deleteObject(
        process.env.THUMBNAIL_BUCKET,
        `${videoId}.${video.thumbnailExtension}`,
      ),
      this.deleteEncodedVideo(videoId),
      this.VideoModel.deleteOne({ _id: videoId }),
    ]);
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
