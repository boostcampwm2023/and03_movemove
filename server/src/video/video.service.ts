/* eslint-disable @typescript-eslint/naming-convention */
/* eslint-disable no-underscore-dangle */
/* eslint-disable class-methods-use-this */
import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model, Types } from 'mongoose';
import { User } from 'src/user/schemas/user.schema';
import { deleteObject } from 'src/ncpAPI/deleteObject';
import { VideoNotFoundException } from 'src/exceptions/video-not-found.exception';
import { NotYourVideoException } from 'src/exceptions/not-your-video.exception';
import axios from 'axios';
import * as _ from 'lodash';
import { ActionService } from 'src/action/action.service';
import { createPresignedUrl } from 'src/ncpAPI/presignedURL';
import { VideoConflictException } from 'src/exceptions/video-conflict.exception';
import { checkUpload, listObjects } from 'src/ncpAPI/listObjects';
import { VideoUploadRequiredException } from 'src/exceptions/video-upload-required-exception copy';
import { ThumbnailUploadRequiredException } from 'src/exceptions/thumbnail-upload-required-exception copy 2';
import { BadRequestFormatException } from 'src/exceptions/bad-request-format.exception';
import { EncodingActionFailException } from 'src/exceptions/encoding-action-fail.exception';
import { GreenEyeActionFailException } from 'src/exceptions/greeneye-action-fail.exception';
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
      { $unwind: '$actions' },
      { $addFields: { videoId: { $toObjectId: '$actions.videoId' } } },
      {
        $lookup: {
          from: 'videos',
          localField: 'videoId',
          foreignField: '_id',
          as: 'video',
        },
      },
      {
        $match: {
          ...(category !== CategoryEnum.전체 && { 'video.category': category }),
        },
      },
      {
        $project: {
          'actions.videoId': 1,
          'actions.seed': 1,
        },
      },
      { $replaceRoot: { newRoot: '$actions' } },
    ]);

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
      videos.map((video) => this.getVideoInfo(video)),
    );
    return { videos: videoInfos, seed: viewSeed };
  }

  async getVideoInfo(video: any): Promise<VideoInfoDto> {
    const { totalRating, raterCount, uploaderId, ...videoInfo } = video;
    const rating = raterCount ? (totalRating / raterCount).toFixed(1) : null;

    const manifest = `${process.env.MANIFEST_URL_PREFIX}/${videoInfo._id}_master.m3u8`;

    const { profileImageExtension, uuid, ...uploaderInfo } =
      '_doc' in uploaderId ? uploaderId._doc : uploaderId; // uploaderId가 model인경우 _doc을 붙여줘야함
    const [profileImageUrl, thumbnailImageUrl] = await Promise.all([
      profileImageExtension
        ? await createPresignedUrl(
            process.env.PROFILE_BUCKET,
            `${uuid}.${profileImageExtension}`,
            'GET',
          )
        : null,
      await createPresignedUrl(
        process.env.THUMBNAIL_BUCKET,
        `${videoInfo._id}.${videoInfo.thumbnailExtension}`,
        'GET',
      ),
    ]);
    const uploader = {
      ...uploaderInfo,
      ...(profileImageUrl && { profileImageUrl }),
      uuid,
    };

    return {
      video: { ...videoInfo, manifest, rating, thumbnailImageUrl },
      uploader,
    };
  }

  async uploadVideo(
    videoDto: VideoDto,
    uuid: string,
    videoId: string,
    accessToken: string,
  ) {
    if (!Types.ObjectId.isValid(videoId)) throw new BadRequestFormatException();
    const checkDuplicate = await this.VideoModel.findOne({ _id: videoId });
    if (checkDuplicate) throw new VideoConflictException();

    const { videoExtension, thumbnailExtension } = videoDto;
    const videoName = `${videoId}.${videoExtension}`;
    const thumbnailName = `${videoId}.${thumbnailExtension}`;

    if (!(await checkUpload(process.env.INPUT_BUCKET, videoName))) {
      throw new VideoUploadRequiredException();
    }
    if (!(await checkUpload(process.env.THUMBNAIL_BUCKET, thumbnailName))) {
      throw new ThumbnailUploadRequiredException();
    }

    const [__, uploader] = await Promise.all([
      this.requestEncodingAPI(
        process.env.INPUT_BUCKET,
        process.env.OUTPUT_BUCKET,
        videoName,
      ),
      this.UserModel.findOne({ uuid }, { _id: 1 }),
    ]);

    const { title, content, category } = videoDto;
    const newVideo = new this.VideoModel({
      _id: videoId,
      title,
      content,
      category,
      uploaderId: uploader._id,
      thumbnailExtension,
      videoExtension,
    });
    await Promise.all([
      newVideo.save(),
      this.requestGreenEyeAPI(
        process.env.INPUT_BUCKET,
        videoName,
        videoId,
        accessToken,
      ),
    ]);
    return { videoId, ...videoDto };
  }

  async requestEncodingAPI(
    inputBucket: string,
    outputBucket: string,
    objectName: string,
  ) {
    const videoUrl = await createPresignedUrl(inputBucket, objectName, 'GET');
    const accessKey = process.env.ACCESS_KEY;
    const secretKey = process.env.SECRET_KEY;
    const data = {
      videoUrl,
      object_name: objectName,
      outputBucket,
      accessKey,
      secretKey,
    };
    try {
      await axios.post(process.env.ENCODING_API_URL, data);
    } catch (error) {
      throw new EncodingActionFailException();
    }
  }

  async requestGreenEyeAPI(
    inputBucket: string,
    videoName: string,
    videoId: string,
    accessToken: string,
  ) {
    const videoUrl = await createPresignedUrl(inputBucket, videoName, 'GET');
    const data = {
      videoUrl,
      object_name: videoId,
      greenEyeSecret: process.env.GREENEYE_SECRET,
      accessToken,
    };

    try {
      await axios.post(process.env.GREENEYE_API_URL, data);
    } catch (error) {
      throw new GreenEyeActionFailException();
    }
  }

  async deleteEncodedVideo(videoId: string) {
    const deleteList = await listObjects(process.env.OUTPUT_BUCKET, {
      prefix: videoId,
    });
    return Promise.all(
      deleteList.map((fileName: string) =>
        deleteObject(process.env.OUTPUT_BUCKET, fileName),
      ),
    );
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
    const confidentNumber = Math.max(raterCountPercentile.raterCount, 1); // 0나누기 방지
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
    const video = await this.VideoModel.findOne(
      { _id: videoId },
      {},
      { lean: true },
    ).populate('uploaderId', '-_id -actions');
    if (!video) {
      throw new VideoNotFoundException();
    }
    return this.getVideoInfo(video);
  }
}
