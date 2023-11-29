import {
  Controller,
  Post,
  Body,
  UseInterceptors,
  Get,
  Query,
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

@ApiTags('COMPLETE')
@Controller()
export class AuthController {
  constructor(private authService: AuthService) {}

  /**
   * 회원가입
   */
  @Post('auth/signup')
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
  @ApiSuccessResponse(201, '토큰 재발급 성공', RefreshResponseDto)
  @ApiFailResponse('인증 실패', [InvalidRefreshTokenException])
  refresh(
    @Body() refreshRequestDto: RefreshRequestDto,
  ): Promise<RefreshResponseDto> {
    return this.authService.refresh(refreshRequestDto);
  }

  @Get('presigned-url/advertisements')
  getAdvertisementPresignedUrl(
    @Query() query: AdvertisementPresignedUrlRequestDto,
  ) {
    return this.authService.getAdvertisementPresignedUrl(query.name);
  }

  @Get('presigned-url/profile')
  getProfilePresignedUrl(@Query() query: ProfilePresignedUrlRequestDto) {
    return this.authService.getProfilePresignedUrl(query);
  }
}
