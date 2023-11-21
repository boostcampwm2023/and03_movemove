import {
  Controller,
  Get,
  Param,
  Put,
  Post,
  Delete,
  Body,
  StreamableFile,
  Header,
  UseInterceptors,
  UploadedFiles,
} from '@nestjs/common';
import { createReadStream } from 'fs';
import { join } from 'path';
import { ApiConsumes } from '@nestjs/swagger';
import { FileFieldsInterceptor } from '@nestjs/platform-express';
import { VideoService } from './video.service';
import { VideoDto } from './dto/video.dto';
import { VideoRatingDTO } from './dto/video-rating.dto';
import { FileExtensionPipe } from './video.pipe';

@Controller('videos')
export class VideoController {
  constructor(
    private videoService: VideoService,
    private fileExtensionPipe: FileExtensionPipe,
  ) {}

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

  @ApiConsumes('multipart/form-data')
  @UseInterceptors(
    FileFieldsInterceptor([
      { name: 'video', maxCount: 1 },
      { name: 'thumbnail', maxCount: 1 },
    ]),
  )
  @Post()
  uploadVideo(
    @UploadedFiles() files: Array<Express.Multer.File>,
    @Body() videoDto: VideoDto,
  ) {
    this.fileExtensionPipe.transform(files);
    return this.videoService.uploadVideo(files, videoDto);
  }

  @Get('top-rated')
  getTopRatedVideo(@Param('category') category: string) {
    return this.videoService.getTopRatedVideo(category);
  }

  @Get('manifest')
  @Header('Content-Type', 'application/json')
  getManifest() {
    const file = createReadStream(join(process.cwd(), 'package.json'));
    return new StreamableFile(file);
  }

  @Get('trend')
  getTrendVideo(@Param('limit') limit: number) {
    return this.videoService.getTrendVideo(limit);
  }

  @Get(':id')
  getVideo(@Param('id') videoId: string) {
    return this.videoService.getVideo(videoId);
  }

  @Delete(':id')
  deleteVideo(@Param('id') videoId: string) {
    return this.videoService.deleteVideo(videoId);
  }
}
