/* eslint-disable class-methods-use-this */
import { Injectable } from '@nestjs/common';
import { VideoDto } from './dto/video.dto';
import { VideoRatingDTO } from './dto/video-rating.dto';
@Injectable()
export class VideoService {
  getRandomVideo(category: string, limit: number) {
    return `get random video ${category} ${limit}`;
  }

  updateVideoRating(videoId: string, videoRatingDto: VideoRatingDTO) {
    return `update video rating ${videoId} ${videoRatingDto}`;
  }

  setVideoRating(videoId: string, videoRatingDto: VideoRatingDTO) {
    return `set video rating ${videoId} ${videoRatingDto}`;
  }

  uploadVideo(videoDto: VideoDto) {
    return `upload video ${videoDto}`;
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
