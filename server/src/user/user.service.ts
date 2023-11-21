import { Injectable } from '@nestjs/common';

@Injectable()
export class UserService {
  getProfile() {
    return 'get profile';
  }

  patchProfile(userDto) {
    return `patch profile ${userDto}`;
  }
}
