import { Controller, Get, Patch, Body, Param, Query } from '@nestjs/common';
import { UserDto } from 'src/user/dto/user.dto';
import { ApiSuccessResponse } from 'src/decorators/api-succes-response';
import { UserService } from './user.service';
import { UserUploadedVideoQueryDto } from './dto/uploaded-video-request.dto';
import { UploadedVideoResponseDto } from './dto/uploaded-video-response.dto';

@Controller('users')
export class UserController {
  constructor(private userService: UserService) {}

  @Get('profile')
  getProfile() {
    return this.userService.getProfile();
  }

  @Patch('profile')
  patchProfile(@Body() userDto: UserDto) {
    return this.userService.patchProfile(userDto);
  }

  /**
   * 특정 유저가 업로드 한 비디오 정보 반환
   */
  @Get(':userId/vidoes/uploaded')
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
