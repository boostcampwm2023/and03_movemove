import {
  Controller,
  Get,
  Patch,
  Body,
  UseGuards,
  Param,
  UseInterceptors,
  UploadedFile,
} from '@nestjs/common';
import { RequestUser, User } from 'src/decorators/request-user';
import { ApiBearerAuth, ApiConsumes } from '@nestjs/swagger';
import { AuthGuard } from 'src/auth/auth.guard';
import { ApiFailResponse } from 'src/decorators/api-fail-response';
import { InvalidTokenException } from 'src/exceptions/invalid-token.exception';
import { TokenExpiredException } from 'src/exceptions/token-expired.exception';
import { FileInterceptor } from '@nestjs/platform-express';
import { ApiSuccessResponse } from 'src/decorators/api-succes-response';
import { UserNotFoundException } from 'src/exceptions/user-not-found.exception';
import { UserService } from './user.service';
import { ProfileDto } from './dto/profile.dto';

@ApiBearerAuth()
@UseGuards(AuthGuard)
@ApiFailResponse('인증 실패', [InvalidTokenException, TokenExpiredException])
@Controller('users')
export class UserController {
  constructor(private userService: UserService) {}

  @Get(':id/profile')
  @ApiSuccessResponse(200, '프로필 조회 성공', ProfileDto)
  @ApiFailResponse('프로필 조회 실패', [UserNotFoundException])
  getProfile(@Param('id') userId: string) {
    return this.userService.getProfile(userId);
  }

  @Patch('profile')
  @ApiConsumes('multipart/form-data')
  @UseInterceptors(FileInterceptor('profileImage'))
  patchProfile(
    @UploadedFile() profileImage: Express.Multer.File,
    @Body() profileDto: ProfileDto,
    @RequestUser() user: User,
  ) {
    return this.userService.patchProfile(profileDto, profileImage, user.id);
  }
}
