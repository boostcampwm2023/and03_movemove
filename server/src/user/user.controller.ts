import { Controller, Get, Patch, Body, UseGuards, Param } from '@nestjs/common';
import { UserDto } from 'src/user/dto/user.dto';
import { RequestUser, User } from 'src/decorators/request-user';
import { ApiBearerAuth } from '@nestjs/swagger';
import { AuthGuard } from 'src/auth/auth.guard';
import { ApiFailResponse } from 'src/decorators/api-fail-response';
import { InvalidTokenException } from 'src/exceptions/invalid-token.exception';
import { TokenExpiredException } from 'src/exceptions/token-expired.exception';
import { UserService } from './user.service';

@ApiBearerAuth()
@UseGuards(AuthGuard)
@ApiFailResponse('인증 실패', [InvalidTokenException, TokenExpiredException])
@Controller('users')
export class UserController {
  constructor(private userService: UserService) {}

  @Get(':id/profile')
  getProfile(@Param('id') userId: string) {
    return this.userService.getProfile(userId);
  }

  @Patch('profile')
  patchProfile(@Body() userDto: UserDto, @RequestUser() user: User) {
    return this.userService.patchProfile(userDto, user.id);
  }
}
