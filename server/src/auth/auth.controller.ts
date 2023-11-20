import {
  Controller,
  Post,
  Body,
  UseInterceptors,
  UploadedFile,
  HttpCode,
  ConflictException,
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { UserDto } from 'src/user/dto/user.dto';
import { FileInterceptor } from '@nestjs/platform-express';
import { ApiAcceptedResponse, ApiBadRequestResponse, ApiConflictResponse, ApiConsumes, ApiCreatedResponse, ApiForbiddenResponse, ApiOkResponse, ApiUnauthorizedResponse } from '@nestjs/swagger';
import { SignupResponseDto } from './dto/signup-response.dto';
import { SignupRequestDto } from './dto/signup-request.dto';
@Controller('users')
export class AuthController {
  constructor(private authService: AuthService) {}

  @Post('signup')
  @ApiConsumes('multipart/form-data')
  @ApiCreatedResponse({
    description: '회원가입 성공',
    type: SignupResponseDto
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
    //profileImage의 버퍼값을 dto에 저장
    //userDto.profileImage = profileImage.buffer;
    return this.authService.create(signupRequestDto, profileImage);
  }

  @Post('login')
  signin(@Body('uuid') uuid: string) {
    return this.authService.signin(uuid);
  }
}
