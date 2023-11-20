import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { UserConflictException } from 'src/exceptions/conflict.exception';
import { putObject } from 'src/ncpAPI/putObject';
import { User } from 'src/user/schemas/user.schema';
import { ProfileResponseDto } from 'src/user/dto/profile-response.dto';
import { JwtService } from '@nestjs/jwt';
import { SignupRequestDto } from './dto/signup-request.dto';
import { SignupResponseDto } from './dto/signup-response.dto';
import { JwtResponseDto } from './dto/jwt-response.dto';

@Injectable()
export class AuthService {
  constructor(
    @InjectModel(User.name) private userModel: Model<User>,
    private jwtService: JwtService,
  ) {}

  async create(
    signupRequestDto: SignupRequestDto,
    profileImage: Express.Multer.File,
  ) {
    const { uuid } = signupRequestDto;
    if ((await this.userModel.find({ uuid }))?.length) {
      throw new UserConflictException();
    }
    const extension = profileImage.originalname.split('.').pop();
    putObject(
      process.env.PROFILE_BUCKET,
      `${uuid}.${extension}`,
      profileImage.buffer,
    );
    const newUser = new this.userModel(signupRequestDto);
    newUser.save();
    const jwt = await this.getTokens(uuid);
    const profile = new ProfileResponseDto();
    return new SignupResponseDto(jwt, profile);
  }

  async getTokens(uuid: string): Promise<JwtResponseDto> {
    return {
      accessToken: await this.jwtService.signAsync({ uuid, isRefresh: false }),
      refreshToken: await this.jwtService.signAsync({ uuid, isRefresh: true }),
    };
  }

  signin(uuid: string) {
    return `signin user ${uuid}`;
  }
}
