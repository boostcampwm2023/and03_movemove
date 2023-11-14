import {
  Controller,
  Get,
  Param,
  Put,
  Post,
  Delete,
  Body,
} from '@nestjs/common';
import { VideoService } from './video.service';
import { VideoRatingDTO, VideoDto } from './dto/video.dto';

@Controller('videos')
export class VideoController {
  constructor(private videoService: VideoService) {}

  @Get('random')
  getRandomVideo(
    @Param('category') category: string,
    @Param('limit') limit: number,
  ) {
    return this.videoService.getRandomVideo(category, limit);
  }

  @Put(':id/rating')
  updateVideoRating(
    @Param('id') videoId: string,
    @Body() videoRatingDto: VideoRatingDTO,
  ) {
    return this.videoService.updateVideoRating(videoId, videoRatingDto);
  }

  @Post(':id/rating')
  setVideoRating(
    @Param('id') videoId: string,
    @Body() videoRatingDto: VideoRatingDTO,
  ) {
    return this.videoService.setVideoRating(videoId, videoRatingDto);
  }

  @Post()
  uploadVideo(@Body() videoDto: VideoDto) {
    return this.videoService.uploadVideo(videoDto);
  }

  @Get(':id')
  getVideo(@Param('id') videoId: string) {
    return this.videoService.getVideo(videoId);
  }

  @Delete(':id')
  deleteVideo(@Param('id') videoId: string) {
    return this.videoService.deleteVideo(videoId);
  }

  @Get('trend')
  getTrendVideo(@Param('limit') limit: number) {
    return this.videoService.getTrendVideo(limit);
  }

  @Get('top-rated')
  getTopRatedVideo(@Param('category') category: string) {
    return this.videoService.getTopRatedVideo(category);
  }
}
