import {
  Controller,
  Post,
  Body,
  UseInterceptors,
  UploadedFile,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { ApiConsumes } from '@nestjs/swagger';
import { ApiSuccessResponse } from 'src/decorators/api-succes-response';
import { ApiFailResponse } from 'src/decorators/api-fail-response';
import { UserConflictException } from 'src/exceptions/conflict.exception';
import { OAuthFailedException } from 'src/exceptions/oauth-failed.exception';
import { LoginFailException } from 'src/exceptions/login-fail.exception';
import { AuthService } from './auth.service';
import { SignupRequestDto } from './dto/signup-request.dto';
import { SignupResponseDto } from './dto/signup-response.dto';
import { SigninResponseDto } from './dto/signin-response.dto';
import { SigninRequestDto } from './dto/signin-request.dto';

@Controller('users')
export class AuthController {
  constructor(private authService: AuthService) {}

  @Post('signup')
  @ApiConsumes('multipart/form-data')
  @ApiSuccessResponse({
    statusCode: 201,
    description: '회원가입 성공',
    model: SignupResponseDto,
  })
  @ApiFailResponse('인증 실패', [OAuthFailedException])
  @ApiFailResponse('회원가입 실패', [UserConflictException])
  @UseInterceptors(FileInterceptor('profileImage'))
  signUp(
    @UploadedFile() profileImage: Express.Multer.File,
    @Body() signupRequestDto: SignupRequestDto,
  ): Promise<SignupResponseDto> {
    return this.authService.create(signupRequestDto, profileImage);
  }

  @Post('login')
  @ApiSuccessResponse({
    statusCode: 201,
    description: '로그인 성공',
    model: SigninResponseDto,
  })
  @ApiFailResponse('인증 실패', [LoginFailException, OAuthFailedException])
  signin(
    @Body() signinRequestDto: SigninRequestDto,
  ): Promise<SigninResponseDto> {
    return this.authService.signin(signinRequestDto);
  }
}
