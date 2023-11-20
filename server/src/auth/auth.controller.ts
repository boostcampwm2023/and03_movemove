import {
  Controller,
  Post,
  Body,
  UseInterceptors,
  UploadedFile,
  UseGuards,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import {
  ApiBadRequestResponse,
  ApiConflictResponse,
  ApiConsumes,
  ApiCreatedResponse,
  ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { AuthService } from './auth.service';
import { SignupResponseDto } from './dto/signup-response.dto';
import { SignupRequestDto } from './dto/signup-request.dto';
import { AuthGuard } from './auth.guard';

@Controller('users')
export class AuthController {
  constructor(private authService: AuthService) {}

  @Post('signup')
  @ApiConsumes('multipart/form-data')
  @ApiCreatedResponse({
    description: '회원가입 성공',
    type: SignupResponseDto,
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
  signin(@Body('uuid') uuid: string) {
    return this.authService.signin(uuid);
  }
}
