import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { UserConflictException } from 'src/exceptions/conflict.exception';
import { putObject } from 'src/ncpAPI/putObject';
import { SignupRequestDto } from './dto/signup-request.dto';
import { User } from 'src/user/schemas/user.schema';

@Injectable()
export class AuthService {
  constructor(@InjectModel(User.name) private userModel: Model<User>) {}
  async create(signupRequestDto: SignupRequestDto, profileImage: Express.Multer.File) {
    if ((await this.userModel.find({ uuid: signupRequestDto.uuid }))?.length) {
      throw new UserConflictException();
    }
    const extension = profileImage.originalname.split('.').pop();
    putObject(
      process.env.PROFILE_BUCKET,
      `${signupRequestDto.uuid}.${extension}`,
      profileImage.buffer,
    );
    const newUser = new this.userModel(signupRequestDto);
    newUser.save();
    return 
  }

  signin(uuid: string) {
    return `signin user ${uuid}`;
  }
}
