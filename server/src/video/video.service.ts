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
import axios from 'axios';
import * as _ from 'lodash';
import { ActionService } from 'src/action/action.service';
import { VideoDto } from './dto/video.dto';
import { Video } from './schemas/video.schema';
import { CategoryEnum } from './enum/category.enum';
import { VideoInfoDto } from './dto/video-info.dto';
import { RandomVideoQueryDto } from './dto/random-video-query.dto';

@Injectable()
export class VideoService {
  constructor(
    @InjectModel('Video') private VideoModel: Model<Video>,
    @InjectModel('User') private UserModel: Model<User>,
    private actionService: ActionService,
  ) {}

  async getRandomVideo(
    { category, limit, seed }: RandomVideoQueryDto,
    userId: string,
  ) {
    const actions = await this.UserModel.aggregate([
      { $match: { uuid: userId } },
      { $project: { 'actions.videoId': 1, 'actions.seed': 1 } },
    ]).then((result) => result.pop().actions);

    const viewIdList = actions.map((userAction) => userAction.videoId);
    const condition = {
      _id: { $not: { $in: viewIdList } },
      ...(category !== CategoryEnum.전체 && { category }),
    };
    // 유저 action을 싹 다 가져옴
    // 1. { $not : { $in : [videoIdList] }} 를 샘플한다.
    // 2. 1번에서 못가져온 만큼 actions에서 seed가 같지 않은 videoIdList를 한번더 만든다.
    // 3. videoIdList를 랜덤 정렬해서 다시 find { $in: [otherSeedIdList]}를 해준다.
    const unviewVideoList = await this.VideoModel.aggregate([
      { $match: condition },
      { $sample: { size: limit } },
    ]);

    const lackVideoCount = limit - unviewVideoList.length;
    const otherSeedIdList = _.sampleSize(
      _.reject(actions, { seed }).map((userAction) => userAction.videoId),
      lackVideoCount,
    );

    const viewVideoList = await this.VideoModel.find(
      {
        _id: { $in: otherSeedIdList },
      },
      {},
      { lean: true },
    );
    const videos = [...unviewVideoList, ...viewVideoList];
    await this.UserModel.populate(videos, {
      path: 'uploaderId',
      select: '-_id -actions',
    });

    const SEED_MAX = 1_000_000;
    const viewSeed = seed ?? Math.floor(Math.random() * SEED_MAX);
    const videoInfos = await Promise.all(
      videos.map((video) => this.getVideoInfo(video, viewSeed)),
    );
    const nextQueryString = new URLSearchParams({
      category,
      limit,
      seed: viewSeed,
    } as any).toString();

    const next = `${process.env.SERVER_URL}videos/random?${nextQueryString}`;
    return { videos: videoInfos, next };
  }

  async getManifest(videoId: string, userId: string, seed: number) {
    this.actionService.viewVideo(videoId, userId, seed);

    const encodingSuffixes = process.env.ENCODING_SUFFIXES.split(',');
    const manifestURL = `${process.env.MANIFEST_URL_PREFIX}${videoId}_,${process.env.ENCODING_SUFFIXES}${process.env.ABR_MANIFEST_URL_SUFFIX}`;
    const manifest: string = await axios
      .get(manifestURL)
      .then((res) => res.data);

    let index = -1;
    const modifiedManifest = manifest.replace(/.*\.m3u8$/gm, () => {
      index += 1;
      return `${process.env.MANIFEST_URL_PREFIX}${videoId}_${encodingSuffixes[index]}${process.env.SBR_MANIFEST_URL_SUFFIX}`;
    });
    return modifiedManifest;
  }

  async getVideoInfo(video: any, seed?: number): Promise<VideoInfoDto> {
    const { totalRating, raterCount, uploaderId, ...videoInfo } = video;
    const rating = (totalRating / raterCount).toFixed(1);
    const manifest = `${process.env.SERVER_URL}videos/${
      videoInfo._id
    }/manifest${seed ? `?seed=${seed}` : ''}`;

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

  async getTrendVideo(limit: number) {
    const fields = Object.keys(this.VideoModel.schema.paths).reduce(
      (acc, field) => {
        acc[field] = 1;
        return acc;
      },
      {},
    );

    const trendVideos = await this.VideoModel.aggregate([
      {
        $project: {
          ...fields,
          trendScore: {
            $divide: [
              '$viewCount',
              { $pow: [{ $subtract: ['$$NOW', '$uploadedAt'] }, 1.8] },
            ],
          },
        },
      },
      { $sort: { trendScore: -1 } },
      { $project: { trendScore: 0 } },
      { $limit: limit },
    ]);

    await this.UserModel.populate(trendVideos, {
      path: 'uploaderId',
      select: '-_id -actions',
    });

    const videos = await Promise.all(
      trendVideos.map((video) => this.getVideoInfo(video)),
    );

    return { videos };
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

    const videos = await Promise.all(
      top10Videos.map((video) => this.getVideoInfo(video)),
    );
    return { videos };
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
