import { Controller, Get, Patch, Body } from '@nestjs/common';
import { UserDto } from 'src/user/dto/user.dto';
import { UserService } from './user.service';

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
}
