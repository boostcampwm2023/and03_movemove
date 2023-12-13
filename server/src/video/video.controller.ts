import {
  Controller,
  Get,
  Param,
  Put,
  Post,
  Delete,
  Body,
  UseInterceptors,
  UseGuards,
  Query,
} from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
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
import { VideoConflictException } from 'src/exceptions/video-conflict.exception';
import { ThumbnailUploadRequiredException } from 'src/exceptions/thumbnail-upload-required-exception copy 2';
import { VideoUploadRequiredException } from 'src/exceptions/video-upload-required-exception copy';
import { BadRequestFormatException } from 'src/exceptions/bad-request-format.exception';
import { EncodingActionFailException } from 'src/exceptions/encoding-action-fail.exception';
import { VideoService } from './video.service';
import { VideoDto } from './dto/video.dto';
import { VideoRatingDTO } from './dto/video-rating.dto';
import { RandomVideoQueryDto } from './dto/random-video-query.dto';
import { VideoSummaryResponseDto } from './dto/video-summary-response.dto';
import { VideoInfoDto } from './dto/video-info.dto';
import { VideoRatingResponseDTO } from './dto/video-rating-response.dto';
import { TopVideoQueryDto } from './dto/top-video-query.dto';
import { VideoListResponseDto } from './dto/video-list-response.dto';
import { RandomVideoResponseDto } from './dto/random-video-response.dto';

@ApiTags('VIDEO')
@ApiBearerAuth()
@UseGuards(AuthGuard)
@ApiFailResponse('인증 실패', [InvalidTokenException, TokenExpiredException])
@Controller('videos')
export class VideoController {
  constructor(
    private videoService: VideoService,
    private actionService: ActionService,
  ) {}

  /**
   * 랜덤으로 비디오 응답
   */
  @Get('random')
  @ApiSuccessResponse(200, '랜덤 비디오 반환 성공', RandomVideoResponseDto)
  getRandomVideo(
    @Query() query: RandomVideoQueryDto,
    @RequestUser() user: User,
  ) {
    console.log(`${user.id} 랜덤 비디오 응답 API : ${JSON.stringify(query)}`);
    return this.videoService.getRandomVideo(query, user.id);
  }

  /**
   * 비디오 인코딩
   */
  @UseInterceptors(
    FileFieldsInterceptor([
      { name: 'video', maxCount: 1 },
      { name: 'thumbnail', maxCount: 1 },
    ]),
  )
  @Post(':videoId')
  @ApiSuccessResponse(201, '비디오 업로드 성공', VideoSummaryResponseDto)
  @ApiFailResponse('중복된 비디오 ID', [VideoConflictException])
  @ApiFailResponse('잘못된 비디오 ID', [BadRequestFormatException])
  @ApiFailResponse('업로드가 필요함', [
    VideoUploadRequiredException,
    ThumbnailUploadRequiredException,
  ])
  @ApiFailResponse('인코딩 실패', [EncodingActionFailException])
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
  getTopRatedVideo(
    @Query() query: TopVideoQueryDto,
    @RequestUser() user: User,
  ) {
    return this.videoService.getTopRatedVideo(query.category, user.id);
  }

  /**
   * 인기 비디오 반환
   */
  @Get('trend')
  @ApiSuccessResponse(200, '비디오 조회 성공', VideoListResponseDto)
  getTrendVideo(@Query('limit') limit: number, @RequestUser() user: User) {
    return this.videoService.getTrendVideo(limit, user.id);
  }

  /**
   * 썸네일 클릭 시 비디오 정보 반환
   */
  @Get(':id')
  @ApiSuccessResponse(200, '비디오 조회 성공', VideoInfoDto)
  @ApiFailResponse('비디오를 찾을 수 없음', [VideoNotFoundException])
  getVideo(@Param('id') videoId: string, @RequestUser() user: User) {
    return this.videoService.getVideo(videoId, user.id);
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
