import { Controller, Get, Param, Query, UseGuards } from '@nestjs/common';
import { ApiBearerAuth, ApiParam, ApiTags } from '@nestjs/swagger';
import { ApiFailResponse } from 'src/decorators/api-fail-response';
import { AuthGuard } from 'src/auth/auth.guard';
import { InvalidTokenException } from 'src/exceptions/invalid-token.exception';
import { TokenExpiredException } from 'src/exceptions/token-expired.exception';
import { ApiSuccessResponse } from 'src/decorators/api-succes-response';
import { RequestUser, User } from 'src/decorators/request-user';
import { ObjectNotFoundException } from 'src/exceptions/object-not-found.exception';
import { PresignedUrlService } from './presigned-url.service';
import { AdvertisementPresignedUrlRequestDto } from './dto/advertisement-presigned-url-request.dto';
import { ProfilePresignedUrlRequestDto } from './dto/profile-presigned-url-request.dto';
import { VIdeoPresignedUrlRequestDto } from './dto/video-presigned-url-request.dto';
import { ReissuePresignedUrlRequestDto } from './dto/reissue-presigned-url-request.dto';
import { AdvertisementPresignedUrlResponseDto } from './dto/advertisement-presigned-url-response.dto';
import { PresignedUrlResponseDto } from './dto/presigned-url-response.dto';
import { VideoPresignedUrlResponseDto } from './dto/video-presigned-url-response.dto';

@ApiTags('PRESIGNED URL')
@ApiBearerAuth()
@UseGuards(AuthGuard)
@ApiFailResponse('인증 실패', [InvalidTokenException, TokenExpiredException])
@Controller('presigned-url')
export class PresignedUrlController {
  constructor(private presignedUrlService: PresignedUrlService) {}

  /**
   * 광고 이미지를 GET하는 url 발급
   */
  @Get('advertisements')
  @ApiSuccessResponse(
    200,
    '광고 이미지 가져오는 url 발급 성공',
    AdvertisementPresignedUrlResponseDto,
  )
  @ApiFailResponse('url 발급 실패', [ObjectNotFoundException])
  getAdvertisementPresignedUrl(
    @Query() query: AdvertisementPresignedUrlRequestDto,
  ) {
    return this.presignedUrlService.getAdvertisementPresignedUrl(query.name);
  }

  /**
   * 프로필 이미지 변경 시 이미지를 PUT하는 url 발급
   */
  @Get('profile')
  @ApiSuccessResponse(
    200,
    '프로필 이미지를 업로드하는 url 발급 성공',
    PresignedUrlResponseDto,
  )
  putProfilePresignedUrl(
    @Query() query: ProfilePresignedUrlRequestDto,
    @RequestUser() user: User,
  ) {
    return this.presignedUrlService.putProfilePresignedUrl(
      user.id,
      query.profileExtension,
    );
  }

  /**
   * 비디오, 썸네일 이미지를 PUT하는 url 발급
   */
  @Get('video')
  @ApiSuccessResponse(
    200,
    '비디오/썸네일을 업로드하는 url 발급 성공',
    VideoPresignedUrlResponseDto,
  )
  putVideoPresignedUrl(@Query() query: VIdeoPresignedUrlRequestDto) {
    return this.presignedUrlService.putVideoPresignedUrl(query);
  }

  /**
   * 만료된 presigned url 재발급
   */
  @Get('reissue/:id')
  @ApiParam({
    name: 'id',
    description: '썸네일 재발급 시 비디오ID, 프로필 재발급 시 유저 UUID',
  })
  @ApiSuccessResponse(200, 'presigned url 재발급 성공', PresignedUrlResponseDto)
  @ApiFailResponse('url 발급 실패', [ObjectNotFoundException])
  getImagePresignedUrl(
    @Param('id') id: string,
    @Query() query: ReissuePresignedUrlRequestDto,
  ) {
    return this.presignedUrlService.getImagePresignedUrl(id, query);
  }

  @Get('test/issue/:id')
  getVideoPresignedUrl(@Param('id') id: string) {
    return this.presignedUrlService.getVideoPresignedUrl(id);
  }
}
