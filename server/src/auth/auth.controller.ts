import { Controller, Post, Body, Get, Query } from '@nestjs/common';
import { ApiTags } from '@nestjs/swagger';
import { ApiSuccessResponse } from 'src/decorators/api-succes-response';
import { ApiFailResponse } from 'src/decorators/api-fail-response';
import { UserConflictException } from 'src/exceptions/conflict.exception';
import { LoginFailException } from 'src/exceptions/login-fail.exception';
import { InvalidRefreshTokenException } from 'src/exceptions/invalid-refresh-token.exception';
import { ProfileUploadRequiredException } from 'src/exceptions/profile-upload-required-exception';
import { PresignedUrlResponseDto } from 'src/presigned-url/dto/presigned-url-response.dto';
import { PresignedUrlService } from 'src/presigned-url/presigned-url.service';
import { SignupProfilePresignedUrlRequestDto } from 'src/presigned-url/dto/signup-profile-presigned-url-request.dto';
import { InvalidGoogldIdTokenException } from 'src/exceptions/invalid-google-idToken.exception';
import { InconsistentGoogldUuidException } from 'src/exceptions/inconsistent-google-uuid.exception';
import { AuthService } from './auth.service';
import { SignupRequestDto } from './dto/signup-request.dto';
import { SignupResponseDto } from './dto/signup-response.dto';
import { SigninResponseDto } from './dto/signin-response.dto';
import { SigninRequestDto } from './dto/signin-request.dto';
import { RefreshRequestDto } from './dto/refresh-request.dto';
import { RefreshResponseDto } from './dto/refresh-response.dto';

@ApiTags('AUTH')
@Controller('auth')
export class AuthController {
  constructor(
    private authService: AuthService,
    private presignedUrlService: PresignedUrlService,
  ) {}

  /**
   * 회원가입
   */
  @Post('signup')
  @ApiSuccessResponse(201, '회원가입 성공', SignupResponseDto)
  @ApiFailResponse('업로드 필요', [ProfileUploadRequiredException])
  @ApiFailResponse('회원가입 실패', [UserConflictException])
  @ApiFailResponse('인증 실패', [
    InvalidGoogldIdTokenException,
    InconsistentGoogldUuidException,
  ])
  signUp(
    @Body() signupRequestDto: SignupRequestDto,
  ): Promise<SignupResponseDto> {
    return this.authService.create(signupRequestDto);
  }

  /**
   * 로그인
   */
  @Post('login')
  @ApiSuccessResponse(201, '로그인 성공', SigninResponseDto)
  @ApiFailResponse('인증 실패', [
    LoginFailException,
    InvalidGoogldIdTokenException,
    InconsistentGoogldUuidException,
  ])
  signin(
    @Body() signinRequestDto: SigninRequestDto,
  ): Promise<SigninResponseDto> {
    return this.authService.signin(signinRequestDto);
  }

  /**
   * 토큰 재발급
   */
  @Post('refresh')
  @ApiSuccessResponse(201, '토큰 재발급 성공', RefreshResponseDto)
  @ApiFailResponse('인증 실패', [InvalidRefreshTokenException])
  refresh(
    @Body() refreshRequestDto: RefreshRequestDto,
  ): Promise<RefreshResponseDto> {
    return this.authService.refresh(refreshRequestDto);
  }

  /**
   * 회원가입 시 프로필 이미지를 PUT하는 url 발급
   */
  @Get('signup/presigned-url/profile')
  @ApiSuccessResponse(
    200,
    '프로필 이미지를 업로드하는 url 발급 성공',
    PresignedUrlResponseDto,
  )
  async putProfilePresignedUrl(
    @Query() query: SignupProfilePresignedUrlRequestDto,
  ) {
    await this.authService.checkUserConflict(query.uuid);
    return this.presignedUrlService.putProfilePresignedUrl(
      query.uuid,
      query.profileExtension,
    );
  }
}
