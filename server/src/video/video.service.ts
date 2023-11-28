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
import { getBucketImage } from 'src/ncpAPI/getBucketImage';
import { VideoDto } from './dto/video.dto';
import { Video } from './schemas/video.schema';
import { CategoryEnum } from './enum/category.enum';

@Injectable()
export class VideoService {
  constructor(
    @InjectModel('Video') private VideoModel: Model<Video>,
    @InjectModel('User') private UserModel: Model<User>,
  ) {}

  async getRandomVideo(category: string, limit: number) {
    const condition = category === CategoryEnum.전체 ? {} : { category };
    const videos = await this.VideoModel.aggregate([
      { $match: condition },
      { $sample: { size: limit } },
    ]);

    await this.UserModel.populate(videos, {
      path: 'uploaderId',
      select: '-_id -actions',
    });

    const videoData = await Promise.all(
      videos.map((video) => this.getVideoInfo(video)),
    );
    return videoData;
  }

  async getVideoInfo(video: any) {
    const { totalRating, raterCount, uploaderId, ...videoInfo } = video;
    const rating = totalRating / raterCount.toFixed(1);
    const manifest = `${process.env.MANIFEST_URL_PREFIX}${videoInfo._id}_,${process.env.ENCODING_SUFFIXES}${process.env.ABR_MANIFEST_URL_SUFFIX}`;

    const { profileImageExtension, uuid, ...uploaderInfo } =
      '_doc' in uploaderId ? uploaderId._doc : uploaderId; // uploaderId가 model인경우 _doc을 붙여줘야함
    const [profileImage, thumbnailImage] = await Promise.all([
      getBucketImage(process.env.PROFILE_BUCKET, profileImageExtension, uuid),
      getBucketImage(
        process.env.THUMBNAIL_BUCKET,
        videoInfo.thumbnailExtension,
        videoInfo._id,
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

    return { _id: newVideo._id, ...videoDto };
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
    const video = await this.VideoModel.findOne({ _id: videoId }).populate(
      'uploaderId',
      '-_id -actions',
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
    return {
      _id: videoId,
      title: video.title,
      content: video.content,
      category: video.category,
    };
  }

  getTrendVideo(limit: number) {
    return `get trend video ${limit}`;
  }

  async getTopRatedVideo(category: string) {
    const videoTotal = await this.VideoModel.aggregate([
      { $match: { category } },
      {
        $group: {
          _id: null,
          count: { $sum: 1 },
          totalRaterCount: { $sum: '$raterCount' },
          totalRating: { $sum: '$totalRating' },
        },
      },
    ]).then((result) => result.pop());

    const avgRating = videoTotal.totalRating / videoTotal.totalRaterCount;
    const percentile = 0.25;
    const raterCountPercentile = await this.VideoModel.find(
      { category },
      { raterCount: 1 },
    )
      .sort({ raterCount: 1 })
      .skip(Math.floor(videoTotal.count * percentile))
      .limit(1)
      .then((result) => result.pop());
    const confidentNumber = raterCountPercentile.raterCount;
    // 베이즈 평균으로 TOP 10 추출
    const top10Videos = await this.VideoModel.aggregate([
      { $match: { category } },
      {
        $project: {
          data: '$$ROOT',
          bayesian_avg: {
            // 베이즈 평균 = (totalRating + confidentNumber * avgRating )/ (raterCount + confidentNumber),
            $divide: [
              { $add: ['$totalRating', confidentNumber * avgRating] },
              { $add: ['$raterCount', confidentNumber] },
            ],
          },
        },
      },
      { $sort: { bayesian_avg: -1 } },
      { $limit: 10 },
      { $replaceRoot: { newRoot: '$data' } },
    ]);
    await this.UserModel.populate(top10Videos, {
      path: 'uploaderId',
      select: '-_id -actions',
    });

    const videoData = await Promise.all(
      top10Videos.map((video) => this.getVideoInfo(video)),
    );
    return videoData;
  }

  async getVideo(videoId: string) {
    const video = await this.VideoModel.findOne({ _id: videoId }).populate(
      'uploaderId',
      '-_id -actions',
    );

    if (!video) {
      throw new VideoNotFoundException();
    }
    return this.getVideoInfo(video.toObject());
  }
}
