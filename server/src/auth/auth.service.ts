import { Inject, Injectable } from '@nestjs/common';
import { Model } from 'mongoose';
import { putObject } from 'src/ncpAPI/putObject';
import { UserDto } from 'src/user/dto/user.dto';
import { User } from 'src/user/schemas/user.schema';

@Injectable()
export class AuthService {
  constructor(
    @Inject('USER_MODEL')
    private userModel: Model<User>,
  ) {}
  create(userDto: UserDto, profileImage: Express.Multer.File) {
    const extension = profileImage.originalname.split('.').pop();
    putObject(
      process.env.PROFILE_BUCKET,
      `${userDto.uuid}.${extension}`,
      profileImage.buffer,
    );
    const newUser = new this.userModel(userDto);
    newUser.save();
    return 'dd';
    // return `create user ${userDto}`;
  }

  signin(uuid: string) {
    return `signin user ${uuid}`;
  }
}
