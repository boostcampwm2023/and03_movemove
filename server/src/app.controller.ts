import { Controller, Get, UseGuards } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { AppService } from './app.service';
import { AuthGuard } from './auth/auth.guard';
import { ApiSuccessResponse } from './decorators/api-succes-response';
import { AdsResponseDto } from './video/dto/ads-response.dto';
import { ApiFailResponse } from './decorators/api-fail-response';
import { InvalidTokenException } from './exceptions/invalid-token.exception';
import { TokenExpiredException } from './exceptions/token-expired.exception';

@Controller()
export class AppController {
  constructor(private appService: AppService) {}

  /**
   * 광고 이미지 응답
   */
  @ApiTags('LEGACY')
  @Get('ads')
  @ApiBearerAuth()
  @ApiSuccessResponse(200, '광고 조회 성공', AdsResponseDto)
  @ApiFailResponse('인증 실패', [InvalidTokenException, TokenExpiredException])
  @UseGuards(AuthGuard)
  getAds(): Promise<{ adImages: any }> {
    return this.appService.getAds();
  }
}
