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
  Query,
} from '@nestjs/common';
import { createReadStream } from 'fs';
import { join } from 'path';
import { ApiBearerAuth, ApiConsumes, ApiTags } from '@nestjs/swagger';
import { FileFieldsInterceptor } from '@nestjs/platform-express';
import { AuthGuard } from 'src/auth/auth.guard';
import { ApiFailResponse } from 'src/decorators/api-fail-response';
import { InvalidTokenException } from 'src/exceptions/invalid-token.exception';
import { TokenExpiredException } from 'src/exceptions/token-expired.exception';
import { VideoNotFoundException } from 'src/exceptions/video-not-found.exception';
import { ApiSuccessResponse } from 'src/decorators/api-succes-response';
import { NotYourVideoException } from 'src/exceptions/not-your-video.exception';
import { RequestUser, User } from 'src/decorators/request-user';
import { ActionService } from 'src/action/action.service';
import { VideoService } from './video.service';
import { VideoDto } from './dto/video.dto';
import { VideoRatingDTO } from './dto/video-rating.dto';
import { FileExtensionPipe } from './video.pipe';
import { RandomVideoQueryDto } from './dto/random-video-query.dto';
import { RandomVideoResponseDto } from './dto/random-video-response.dto';
import { VideoSummaryResponseDto } from './dto/video-summary-response.dto';
import { VideoResponseDto } from './dto/video-response.dto';
import { VideoInfoDto } from './dto/video-info.dto';

@ApiBearerAuth()
@UseGuards(AuthGuard)
@ApiFailResponse('인증 실패', [InvalidTokenException, TokenExpiredException])
@Controller('videos')
export class VideoController {
  constructor(
    private videoService: VideoService,
    private actionService: ActionService,
    private fileExtensionPipe: FileExtensionPipe,
  ) {}

  @ApiTags('COMPLETE')
  @Get('random')
  @ApiSuccessResponse(200, '랜덤 비디오 반환 성공', RandomVideoResponseDto)
  getRandomVideo(@Query() query: RandomVideoQueryDto) {
    return this.videoService.getRandomVideo(query.category, query.limit);
  }

  @Put(':id/rating')
  updateVideoRating(
    @Param('id') videoId: string,
    @Body() videoRatingDto: VideoRatingDTO,
    @RequestUser() user: User,
  ) {
    return this.actionService.ratingVideo(videoId, videoRatingDto, user.id);
  }

  @ApiConsumes('multipart/form-data')
  @UseInterceptors(
    FileFieldsInterceptor([
      { name: 'video', maxCount: 1 },
      { name: 'thumbnail', maxCount: 1 },
    ]),
  )
  @ApiTags('COMPLETE')
  @Post()
  @ApiSuccessResponse(201, '비디오 업로드 성공', VideoSummaryResponseDto)
  uploadVideo(
    @UploadedFiles() files: Array<Express.Multer.File>,
    @Body() videoDto: VideoDto,
    @RequestUser() user: User,
  ) {
    this.fileExtensionPipe.transform(files);
    return this.videoService.uploadVideo(files, videoDto, user.id);
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
  @ApiSuccessResponse(200, '비디오 조회 성공', VideoInfoDto)
  @ApiFailResponse('비디오를 찾을 수 없음', [VideoNotFoundException])
  getVideo(@Param('id') videoId: string) {
    return this.videoService.getVideo(videoId);
  }

  @Delete(':id')
  @ApiTags('COMPLETE')
  @ApiSuccessResponse(200, '비디오 삭제 성공', VideoSummaryResponseDto)
  @ApiFailResponse('업로더만이 삭제할 수 있음', [NotYourVideoException])
  @ApiFailResponse('비디오를 찾을 수 없음', [VideoNotFoundException])
  deleteVideo(@Param('id') videoId: string, @RequestUser() user: User) {
    return this.videoService.deleteVideo(videoId, user.id);
  }
}
