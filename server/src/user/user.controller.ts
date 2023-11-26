import {
  Controller,
  Get,
  Patch,
  Body,
  UseGuards,
  Param,
  UseInterceptors,
  UploadedFile,
  Query,
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
import { UploadedVideoResponseDto } from './dto/uploaded-video-response.dto';
import { UserUploadedVideoQueryDto } from './dto/uploaded-video-request.dto';

@ApiBearerAuth()
@UseGuards(AuthGuard)
@ApiFailResponse('인증 실패', [InvalidTokenException, TokenExpiredException])
@Controller('users')
export class UserController {
  constructor(private userService: UserService) {}

  @Get(':userId/profile')
  @ApiSuccessResponse(200, '프로필 조회 성공', ProfileDto)
  @ApiFailResponse('프로필 조회 실패', [UserNotFoundException])
  getProfile(@Param('userId') userId: string) {
    return this.userService.getProfile(userId);
  }

  @ApiConsumes('multipart/form-data')
  @UseInterceptors(FileInterceptor('profileImage'))
  @ApiSuccessResponse(200, '프로필 변경 성공', ProfileDto)
  @Patch('profile')
  patchProfile(
    @UploadedFile() profileImage: Express.Multer.File,
    @Body() profileDto: ProfileDto,
    @RequestUser() user: User,
  ) {
    return this.userService.patchProfile(profileDto, profileImage, user.id);
  }

  /**
   * 특정 유저가 업로드 한 비디오 정보 반환
   */
  @Get(':userId/videos/uploaded')
  @ApiSuccessResponse(200, '비디오 반환 성공', UploadedVideoResponseDto)
  getUploadedVideos(
    @Param('userId') userId: string,
    @Query() query: UserUploadedVideoQueryDto,
  ): Promise<UploadedVideoResponseDto> {
    return this.userService.getUploadedVideos(
      userId,
      query.limit,
      query.lastId,
    );
  }
}
