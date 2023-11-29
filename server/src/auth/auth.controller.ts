import {
  Controller,
  Post,
  Body,
  UseInterceptors,
  Get,
  Query,
  Param,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { ApiConsumes, ApiTags } from '@nestjs/swagger';
import { ApiSuccessResponse } from 'src/decorators/api-succes-response';
import { ApiFailResponse } from 'src/decorators/api-fail-response';
import { UserConflictException } from 'src/exceptions/conflict.exception';
import { OAuthFailedException } from 'src/exceptions/oauth-failed.exception';
import { LoginFailException } from 'src/exceptions/login-fail.exception';
import { InvalidRefreshTokenException } from 'src/exceptions/invalid-refresh-token.exception';
import { AuthService } from './auth.service';
import { SignupRequestDto } from './dto/signup-request.dto';
import { SignupResponseDto } from './dto/signup-response.dto';
import { SigninResponseDto } from './dto/signin-response.dto';
import { SigninRequestDto } from './dto/signin-request.dto';
import { RefreshRequestDto } from './dto/refresh-request.dto';
import { RefreshResponseDto } from './dto/refresh-response.dto';
import { AdvertisementPresignedUrlRequestDto } from './dto/advertisement-presigned-url-request.dto';
import { ProfilePresignedUrlRequestDto } from './dto/profile-presigned-url-request.dto';
import { VIdeoPresignedUrlRequestDto } from './dto/video-presigned-url-request.dto';
import { ReissuePresignedUrlRequestDto } from './dto/reissue-presigned-url-request.dto';

@Controller()
export class AuthController {
  constructor(private authService: AuthService) {}

  /**
   * 회원가입
   */
  @Post('auth/signup')
  @ApiTags('AUTH')
  @ApiConsumes('multipart/form-data')
  @ApiSuccessResponse(201, '회원가입 성공', SignupResponseDto)
  @ApiFailResponse('인증 실패', [OAuthFailedException])
  @ApiFailResponse('회원가입 실패', [UserConflictException])
  @UseInterceptors(FileInterceptor('profileImage'))
  signUp(
    @Body() signupRequestDto: SignupRequestDto,
  ): Promise<SignupResponseDto> {
    return this.authService.create(signupRequestDto);
  }

  /**
   * 로그인
   */
  @Post('auth/login')
  @ApiTags('AUTH')
  @ApiSuccessResponse(201, '로그인 성공', SigninResponseDto)
  @ApiFailResponse('인증 실패', [LoginFailException, OAuthFailedException])
  signin(
    @Body() signinRequestDto: SigninRequestDto,
  ): Promise<SigninResponseDto> {
    return this.authService.signin(signinRequestDto);
  }

  /**
   * 토큰 재발급
   */
  @Post('auth/refresh')
  @ApiTags('AUTH')
  @ApiSuccessResponse(201, '토큰 재발급 성공', RefreshResponseDto)
  @ApiFailResponse('인증 실패', [InvalidRefreshTokenException])
  refresh(
    @Body() refreshRequestDto: RefreshRequestDto,
  ): Promise<RefreshResponseDto> {
    return this.authService.refresh(refreshRequestDto);
  }

  /**
   * 광고 이미지를 GET하는 url 발급
   */
  @Get('presigned-url/advertisements')
  @ApiTags('PRESIGNED URL')
  getAdvertisementPresignedUrl(
    @Query() query: AdvertisementPresignedUrlRequestDto,
  ) {
    return this.authService.getAdvertisementPresignedUrl(query.name);
  }

  /**
   * 프로필 이미지를 PUT하는 url 발급
   */
  @Get('presigned-url/profile')
  @ApiTags('PRESIGNED URL')
  putProfilePresignedUrl(@Query() query: ProfilePresignedUrlRequestDto) {
    return this.authService.putProfilePresignedUrl(query);
  }

  /**
   * 비디오, 썸네일 이미지를 PUT하는 url 발급
   */
  @Get('presigned-url/video')
  @ApiTags('PRESIGNED URL')
  putVideoPresignedUrl(@Query() query: VIdeoPresignedUrlRequestDto) {
    return this.authService.putVideoPresignedUrl(query);
  }

  /**
   * 만료된 presigned url 재발급
   */
  @Get('presigned-url/reissue/:id')
  getImagePresignedUrl(
    @Param('id') id: string,
    @Query() query: ReissuePresignedUrlRequestDto,
  ) {
    return this.authService.getImagePresignedUrl(id, query);
  }
}
