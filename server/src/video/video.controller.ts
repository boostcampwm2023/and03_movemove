import {
  Controller,
  Get,
  Param,
  Put,
  Post,
  Delete,
  Body,
  Header,
  UseInterceptors,
  UseGuards,
  Query,
} from '@nestjs/common';
import {
  ApiBearerAuth,
  ApiOkResponse,
  ApiProduces,
  ApiTags,
} from '@nestjs/swagger';
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
import { NeverViewVideoException } from 'src/exceptions/never-view-video.exception';
import { IgnoreInterceptor } from 'src/decorators/ignore-interceptor';
import { ManifestQueryDto } from 'src/action/dto/manifest-query.dto';
import { VideoService } from './video.service';
import { VideoDto } from './dto/video.dto';
import { VideoRatingDTO } from './dto/video-rating.dto';
import { FileExtensionPipe } from './video.pipe';
import { RandomVideoQueryDto } from './dto/random-video-query.dto';
import { VideoSummaryResponseDto } from './dto/video-summary-response.dto';
import { VideoInfoDto } from './dto/video-info.dto';
import { VideoRatingResponseDTO } from './dto/video-rating-response.dto';
import { TopVideoQueryDto } from './dto/top-video-query.dto';
import { VideoListResponseDto } from './dto/video-list-response.dto';

@ApiTags('VIDEO')
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

  /**
   * 랜덤으로 비디오 응답
   */
  @Get('random')
  @ApiSuccessResponse(200, '랜덤 비디오 반환 성공', VideoListResponseDto)
  getRandomVideo(@Query() query: RandomVideoQueryDto) {
    return this.videoService.getRandomVideo(query.category, query.limit);
  }

  /**
   * 비디오 업로드
   */
  @UseInterceptors(
    FileFieldsInterceptor([
      { name: 'video', maxCount: 1 },
      { name: 'thumbnail', maxCount: 1 },
    ]),
  )
  @Post(':videoId')
  @ApiSuccessResponse(201, '비디오 업로드 성공', VideoSummaryResponseDto)
  uploadVideo(
    @Body() videoDto: VideoDto,
    @RequestUser() user: User,
    @Param('videoId') videoId: string,
  ) {
    return this.videoService.uploadVideo(videoDto, user.id, videoId);
  }

  /**
   * 카테고리별 TOP 10 조회
   */
  @Get('top-rated')
  @ApiSuccessResponse(200, 'TOP 10 조회 성공', VideoListResponseDto)
  getTopRatedVideo(@Query() query: TopVideoQueryDto) {
    return this.videoService.getTopRatedVideo(query.category);
  }

  /**
   * Manifest 파일 반환
   */
  @IgnoreInterceptor()
  @Get(':id/manifest')
  @ApiProduces('application/vnd.apple.mpegurl')
  @ApiOkResponse({
    type: String,
    description: '비디오 Manifest 파일',
  })
  @ApiFailResponse('비디오를 찾을 수 없음', [VideoNotFoundException])
  @Header('content-type', 'application/vnd.apple.mpegurl')
  getManifest(
    @Param('id') videoId: string,
    @RequestUser() user: User,
    @Query() manifestQueryDto: ManifestQueryDto,
  ) {
    return this.videoService.getManifest(
      videoId,
      user.id,
      manifestQueryDto.seed,
    );
  }

  /**
   * 인기 비디오 반환
   */
  @Get('trend')
  @ApiSuccessResponse(200, '비디오 조회 성공', VideoListResponseDto)
  getTrendVideo(@Query('limit') limit: number) {
    return this.videoService.getTrendVideo(limit);
  }

  /**
   * 썸네일 클릭 시 비디오 정보 반환
   */
  @Get(':id')
  @ApiSuccessResponse(200, '비디오 조회 성공', VideoInfoDto)
  @ApiFailResponse('비디오를 찾을 수 없음', [VideoNotFoundException])
  getVideo(@Param('id') videoId: string) {
    return this.videoService.getVideo(videoId);
  }

  /**
   * 비디오 삭제
   */
  @Delete(':id')
  @ApiSuccessResponse(200, '비디오 삭제 성공', VideoSummaryResponseDto)
  @ApiFailResponse('업로더만이 삭제할 수 있음', [NotYourVideoException])
  @ApiFailResponse('비디오를 찾을 수 없음', [VideoNotFoundException])
  deleteVideo(@Param('id') videoId: string, @RequestUser() user: User) {
    return this.videoService.deleteVideo(videoId, user.id);
  }

  /**
   * 비디오 별점 등록/수정
   */
  @Put(':id/rating')
  @ApiSuccessResponse(200, '비디오 별점 등록/수정 성공', VideoRatingResponseDTO)
  @ApiFailResponse('비디오를 찾을 수 없음', [VideoNotFoundException])
  @ApiFailResponse('별점 등록 실패', [NeverViewVideoException])
  updateVideoRating(
    @Param('id') videoId: string,
    @Body() videoRatingDto: VideoRatingDTO,
    @RequestUser() user: User,
  ) {
    return this.actionService.ratingVideo(videoId, videoRatingDto, user.id);
  }
}
