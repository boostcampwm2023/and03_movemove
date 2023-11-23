import { Injectable } from '@nestjs/common';
import { ProfileDto } from './dto/profile.dto';

@Injectable()
export class UserService {
  getProfile(userId: string) {
    return `get profile ${userId}`;
  }

  patchProfile(
    profileDto: ProfileDto,
    profileImage: Express.Multer.File,
    uuid: string,
  ) {
    return `patch profile ${profileDto} ${uuid}`;
  }
}
