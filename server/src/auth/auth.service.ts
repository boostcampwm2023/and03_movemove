import { Injectable } from '@nestjs/common';
import { UserDto } from 'src/user/dto/user.dto';

@Injectable()
export class AuthService {
  create(userDto: UserDto) {
    return `create user ${userDto}`;
  }

  signin(uuid: string) {
    return `signin user ${uuid}`;
  }
}
