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
  UseGuards,
  Req,
} from '@nestjs/common';
import { createReadStream } from 'fs';
import { join } from 'path';
import { ApiBearerAuth, ApiConsumes } from '@nestjs/swagger';
import { FileFieldsInterceptor } from '@nestjs/platform-express';
import { AuthGuard } from 'src/auth/auth.guard';
import { ApiFailResponse } from 'src/decorators/api-fail-response';
import { InvalidTokenException } from 'src/exceptions/invalid-token.exception';
import { TokenExpiredException } from 'src/exceptions/token-expired.exception';
import { VideoNotFoundException } from 'src/exceptions/video-not-found.exception';
import { ApiSuccessResponse } from 'src/decorators/api-succes-response';
import { Request } from 'express';
import { VideoService } from './video.service';
import { VideoDto } from './dto/video.dto';
import { VideoRatingDTO } from './dto/video-rating.dto';
import { FileExtensionPipe } from './video.pipe';

@ApiBearerAuth()
@UseGuards(AuthGuard)
@ApiFailResponse('인증 실패', [InvalidTokenException, TokenExpiredException])
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
    @Req() req: Request & { user: { id: string } },
  ) {
    this.fileExtensionPipe.transform(files);
    return this.videoService.uploadVideo(files, videoDto, req.user.id);
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
  @ApiSuccessResponse(200, '비디오 삭제 성공')
  @ApiFailResponse('비디오를 찾을 수 없음', [VideoNotFoundException])
  deleteVideo(
    @Param('id') videoId: string,
    @Req() req: Request & { user: { id: string } },
  ) {
    return this.videoService.deleteVideo(videoId, req.user.id);
  }
}
