import {
  Controller,
  Post,
  Body,
  UseInterceptors,
  UploadedFile,
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

@ApiTags('COMPLETE')
@Controller('auth')
export class AuthController {
  constructor(private authService: AuthService) {}

  /**
   * 회원가입
   */
  @Post('signup')
  @ApiConsumes('multipart/form-data')
  @ApiSuccessResponse(201, '회원가입 성공', SignupResponseDto)
  @ApiFailResponse('인증 실패', [OAuthFailedException])
  @ApiFailResponse('회원가입 실패', [UserConflictException])
  @UseInterceptors(FileInterceptor('profileImage'))
  signUp(
    @UploadedFile() profileImage: Express.Multer.File,
    @Body() signupRequestDto: SignupRequestDto,
  ): Promise<SignupResponseDto> {
    return this.authService.create(signupRequestDto, profileImage);
  }

  /**
   * 로그인
   */
  @Post('login')
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
  @Post('refresh')
  @ApiSuccessResponse(201, '토큰 재발급 성공', RefreshResponseDto)
  @ApiFailResponse('인증 실패', [InvalidRefreshTokenException])
  refresh(
    @Body() refreshRequestDto: RefreshRequestDto,
  ): Promise<RefreshResponseDto> {
    return this.authService.refresh(refreshRequestDto);
  }
}
