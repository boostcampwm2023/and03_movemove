import {
  Controller,
  Post,
  Body,
  UseInterceptors,
  UploadedFile,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import {
  ApiBadRequestResponse,
  ApiConflictResponse,
  ApiConsumes,
  ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { ApiSuccessResponse } from 'src/decorators/api-succes-response';
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
  @ApiBadRequestResponse()
  @ApiUnauthorizedResponse()
  @ApiConflictResponse({
    description: '중복 회원가입',
  })
  @UseInterceptors(FileInterceptor('profileImage'))
  signUp(
    @UploadedFile() profileImage: Express.Multer.File,
    @Body() signupRequestDto: SignupRequestDto,
  ) {
    // profileImage의 버퍼값을 dto에 저장
    // userDto.profileImage = profileImage.buffer;
    return this.authService.create(signupRequestDto, profileImage);
  }

  @Post('login')
  @ApiSuccessResponse({
    statusCode: 201,
    description: '로그인 성공',
    model: SigninResponseDto,
  })
  signin(@Body() signinRequestDto: SigninRequestDto) {
    return this.authService.signin(signinRequestDto);
  }
}
