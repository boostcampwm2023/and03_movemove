import { Injectable } from '@nestjs/common';
import { Model } from 'mongoose';
import { InjectModel } from '@nestjs/mongoose';
import { UserNotFoundException } from 'src/exceptions/user-not-found.exception';
import { getObject } from 'src/ncpAPI/getObject';
import { putObject } from 'src/ncpAPI/putObject';
import * as _ from 'lodash';
import { deleteObject } from 'src/ncpAPI/deleteObject';
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

  async patchProfile(
    profileDto: ProfileDto,
    profileImage: Express.Multer.File,
    uuid: string,
  ): Promise<ProfileDto> {
    const updateOption = _.omitBy(profileDto, _.isEmpty); // profileDto 중 빈 문자열인 필드 제거
    const user = await this.UserModel.findOne({ uuid });
    if (user.nickname === updateOption.nickname) {
      // 실제로 변경된 필드만 updateOption에 남기기
      delete updateOption.nickname;
    }
    if (user.statusMessage === updateOption.statusMessage) {
      delete updateOption.statusMessage;
    }

    let updatedProfileImage;
    if (profileImage) {
      const profileImageExtension = profileImage.originalname.split('.').pop();
      putObject(
        process.env.PROFILE_BUCKET,
        `${uuid}.${profileImageExtension}`,
        profileImage.buffer,
      );
      updateOption.profileImageExtension = profileImageExtension;
      updatedProfileImage = profileImage.buffer;
    } else if (
      'profileImage' in profileDto &&
      user.profileImageExtension !== null
    ) {
      // profileImage 필드를 빈 문자열로 주었고, 기존 프로필이미지가 있었다면 삭제
      deleteObject(
        process.env.PROFILE_BUCKET,
        `${uuid}.${user.profileImageExtension}`,
      );
      updateOption.profileImageExtension = null;
      updatedProfileImage = null;
    }

    await this.UserModel.updateOne({ uuid }, updateOption);
    if (updatedProfileImage !== undefined) {
      updateOption.profileImage = updatedProfileImage;
      delete updateOption.profileImageExtension;
    }
    return updateOption;
  }
}
