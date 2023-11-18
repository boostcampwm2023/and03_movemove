import {
  Controller,
  Post,
  Body,
  UseInterceptors,
  UploadedFile,
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { UserDto } from 'src/user/dto/user.dto';
import { FileInterceptor } from '@nestjs/platform-express';
import { ApiConsumes } from '@nestjs/swagger';
@Controller('users')
export class AuthController {
  constructor(private authService: AuthService) {}

  @Post('signup')
  @ApiConsumes('multipart/form-data')
  @UseInterceptors(FileInterceptor('profileImage'))
  signUp(
    @UploadedFile() profileImage: Express.Multer.File,
    @Body() userDto: UserDto,
  ): string {
    //profileImage의 버퍼값을 dto에 저장
    // userDto.profileImage = profileImage.buffer;
    return this.authService.create(userDto, profileImage);
  }

  @Post('login')
  signin(@Body('uuid') uuid: string) {
    return this.authService.signin(uuid);
  }
}
