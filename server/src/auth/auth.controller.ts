import { Controller, Post, Body } from '@nestjs/common';
import { AuthService } from './auth.service';
import { UserDto } from 'src/user/dto/user.dto';

@Controller('users')
export class AuthController {
  constructor(private authService: AuthService) {}

  @Post('signup')
  create(@Body() userDto: UserDto) {
    return this.authService.create(userDto);
  }

  @Post('login')
  signin(@Body('uuid') uuid: string) {
    return this.authService.signin(uuid);
  }
}
