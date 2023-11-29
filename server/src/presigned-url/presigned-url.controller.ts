import { Controller, Get, Param, Query, UseGuards } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { ApiFailResponse } from 'src/decorators/api-fail-response';
import { AuthGuard } from 'src/auth/auth.guard';
import { InvalidTokenException } from 'src/exceptions/invalid-token.exception';
import { TokenExpiredException } from 'src/exceptions/token-expired.exception';
import { PresignedUrlService } from './presigned-url.service';
import { AdvertisementPresignedUrlRequestDto } from './dto/advertisement-presigned-url-request.dto';
import { ProfilePresignedUrlRequestDto } from './dto/profile-presigned-url-request.dto';
import { VIdeoPresignedUrlRequestDto } from './dto/video-presigned-url-request.dto';
import { ReissuePresignedUrlRequestDto } from './dto/reissue-presigned-url-request.dto';

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
  getAdvertisementPresignedUrl(
    @Query() query: AdvertisementPresignedUrlRequestDto,
  ) {
    return this.presignedUrlService.getAdvertisementPresignedUrl(query.name);
  }

  /**
   * 프로필 이미지를 PUT하는 url 발급
   */
  @Get('profile')
  putProfilePresignedUrl(@Query() query: ProfilePresignedUrlRequestDto) {
    return this.presignedUrlService.putProfilePresignedUrl(query);
  }

  /**
   * 비디오, 썸네일 이미지를 PUT하는 url 발급
   */
  @Get('video')
  putVideoPresignedUrl(@Query() query: VIdeoPresignedUrlRequestDto) {
    return this.presignedUrlService.putVideoPresignedUrl(query);
  }

  /**
   * 만료된 presigned url 재발급
   */
  @Get('reissue/:id')
  getImagePresignedUrl(
    @Param('id') id: string,
    @Query() query: ReissuePresignedUrlRequestDto,
  ) {
    return this.presignedUrlService.getImagePresignedUrl(id, query);
  }
}
