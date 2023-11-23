import { Injectable } from '@nestjs/common';
import { UserDto } from './dto/user.dto';

@Injectable()
export class UserService {
  getProfile(userId: string) {
    return `get profile ${userId}`;
  }

  patchProfile(userDto: UserDto, uuid: string) {
    return `patch profile ${userDto} ${uuid}`;
  }
}
