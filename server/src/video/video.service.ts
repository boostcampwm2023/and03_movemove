/* eslint-disable @typescript-eslint/naming-convention */
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
    const condition = category === '전체' ? {} : { category };
    const videos = await this.VideoModel.aggregate([
      { $match: condition },
      { $sample: { size: limit } },
      { $project: { __v: 0 } },
    ]);

    await this.UserModel.populate(videos, {
      path: 'uploaderId',
      select: '-_id -actions -__v',
    });

    const videoData = await Promise.all(
      videos.map((video) => this.getVideoInfo(video)),
    );
    return videoData;
  }

  async getVideoInfo(video: any) {
    const { totalRating, raterCount, uploaderId, ...videoInfo } = video;
    const rating = totalRating / raterCount.toFixed(1);
    const manifest = `${process.env.MANIFEST_URL_PREFIX}${videoInfo._id}_,${process.env.ENCODING_SUFFIXES}${process.env.MANIFEST_URL_SUFFIX}`;

    const { profileImageExtension, uuid, ...uploaderInfo } =
      '_doc' in uploaderId ? uploaderId._doc : uploaderId; // uploaderId가 model인경우 _doc을 붙여줘야함
    const [profileImage, thumbnailImage] = await Promise.all([
      profileImageExtension
        ? getObject(
            process.env.PROFILE_BUCKET,
            `${uuid}.${profileImageExtension}`,
          )
        : null,
      getObject(
        process.env.THUMBNAIL_BUCKET,
        `${videoInfo._id}.${videoInfo.thumbnailExtension}`,
      ),
    ]);

    const uploader = {
      ...uploaderInfo,
      ...(profileImage && { profileImage }),
      uuid,
    };

    return {
      video: { ...videoInfo, manifest, rating, thumbnailImage },
      uploader,
    };
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

    return { video: { _id: newVideo._id, ...videoDto } };
  }

  async deleteEncodedVideo(videoId: string) {
    const encodingSuffixes = process.env.ENCODING_SUFFIXES.split(',');
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
    const video = await this.VideoModel.findOne(
      { _id: videoId },
      '-__v',
    ).populate('uploaderId', '-_id -actions -__v');
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
    return this.getVideoInfo(video.toObject());
  }

  getTrendVideo(limit: number) {
    return `get trend video ${limit}`;
  }

  getTopRatedVideo(category: string) {
    return `get top rated video ${category}`;
  }

  async getVideo(videoId: string) {
    const video = await this.VideoModel.findOne(
      { _id: videoId },
      '-__v',
    ).populate('uploaderId', '-_id -actions -__v');

    if (!video) {
      throw new VideoNotFoundException();
    }
    return this.getVideoInfo(video.toObject());
  }
}
