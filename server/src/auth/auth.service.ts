import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { UserConflictException } from 'src/exceptions/conflict.exception';
import { putObject } from 'src/ncpAPI/putObject';
import { UserDto } from 'src/user/dto/user.dto';
import { User } from 'src/user/schemas/user.schema';

@Injectable()
export class AuthService {
  constructor(@InjectModel(User.name) private userModel: Model<User>) {}
  async create(userDto: UserDto, profileImage: Express.Multer.File) {
    if (await this.userModel.find({ uuid: userDto.uuid })) {
      throw new UserConflictException();
    }
    const extension = profileImage.originalname.split('.').pop();
    putObject(
      process.env.PROFILE_BUCKET,
      `${userDto.uuid}.${extension}`,
      profileImage.buffer,
    );
    const newUser = new this.userModel(userDto);
    newUser.save();
    return '33';
    // return `create user ${userDto}`;
  }

  signin(uuid: string) {
    return `signin user ${uuid}`;
  }
}
