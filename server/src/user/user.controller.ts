import { Controller, Get, Patch, Body, Param, Query } from '@nestjs/common';
import { UserDto } from 'src/user/dto/user.dto';
import { UserService } from './user.service';
import { UserUploadedVideoQueryDto } from './dto/user-uploaded-video.dto';

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

  @Get(':userId/vidoes/uploaded')
  getUploadedVideos(
    @Param('userId') userId: string,
    @Query() query: UserUploadedVideoQueryDto,
  ) {
    console.log(query);
    return this.userService.getUploadedVideos(
      userId,
      query.limit,
      query.lastId,
    );
  }
}
