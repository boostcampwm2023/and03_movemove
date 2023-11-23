import { Injectable } from '@nestjs/common';
import { Model } from 'mongoose';
import { InjectModel } from '@nestjs/mongoose';
import { UserNotFoundException } from 'src/exceptions/user-not-found.exception';
import { getObject } from 'src/ncpAPI/getObject';
import { ProfileDto } from './dto/profile.dto';
import { User } from './schemas/user.schema';

@Injectable()
export class UserService {
  constructor(@InjectModel(User.name) private UserModel: Model<User>) {}

  async getProfile(userId: string) {
    const user = await this.UserModel.findOne({ uuid: userId });

    if (!user) {
      throw new UserNotFoundException();
    }
    const { uuid, profileImageExtension, nickname, statusMessage } = user;
    const profileImage = profileImageExtension
      ? await getObject(
          process.env.PROFILE_BUCKET,
          `${uuid}.${profileImageExtension}`,
        )
      : null;
    return new ProfileDto({
      nickname,
      statusMessage,
      ...(profileImage && { profileImage }),
    });
  }

  patchProfile(
    profileDto: ProfileDto,
    profileImage: Express.Multer.File,
    uuid: string,
  ) {
    return `patch profile ${profileDto} ${uuid}`;
  }
}
