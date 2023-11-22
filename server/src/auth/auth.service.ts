import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { UserConflictException } from 'src/exceptions/conflict.exception';
import { putObject } from 'src/ncpAPI/putObject';
import { User } from 'src/user/schemas/user.schema';
import { ProfileResponseDto } from 'src/user/dto/profile-response.dto';
import { JwtService } from '@nestjs/jwt';
import { LoginFailException } from 'src/exceptions/login-fail.exception';
import { InvalidRefreshTokenException } from 'src/exceptions/invalid-refresh-token.exception';
import { SignupRequestDto } from './dto/signup-request.dto';
import { JwtResponseDto } from './dto/jwt-response.dto';
import { SignupResponseDto } from './dto/signup-response.dto';
import { SigninResponseDto } from './dto/signin-response.dto';
import { SigninRequestDto } from './dto/signin-request.dto';
import { RefreshRequestDto } from './dto/refresh-request.dto';
import { RefreshResponseDto } from './dto/refresh-response.dto';

@Injectable()
export class AuthService {
  constructor(
    @InjectModel(User.name) private UserModel: Model<User>,
    private jwtService: JwtService,
  ) {}

  async create(
    signupRequestDto: SignupRequestDto,
    profileImage: Express.Multer.File,
  ): Promise<SignupResponseDto> {
    const { uuid } = signupRequestDto;
    if (await this.UserModel.findOne({ uuid })) {
      throw new UserConflictException();
    }
    const extension = profileImage.originalname.split('.').pop();
    putObject(
      process.env.PROFILE_BUCKET,
      `${uuid}.${extension}`,
      profileImage.buffer,
    );
    const newUser = new this.UserModel(signupRequestDto);
    newUser.save();
    const jwt = await this.getTokens(uuid);
    const profile = new ProfileResponseDto({
      uuid: newUser.uuid,
      nickname: newUser.nickname,
      statusMessage: newUser.statusMessage,
    });
    return new SignupResponseDto({ jwt, profile });
  }

  async getTokens(uuid: string): Promise<JwtResponseDto> {
    return {
      accessToken: await this.jwtService.signAsync(
        { id: uuid },
        { expiresIn: process.env.JWT_SECRET_EXPIRATION_TIME },
      ),
      refreshToken: await this.jwtService.signAsync(
        { id: uuid },
        {
          secret: process.env.JWT_REFRESH_SECRET,
          expiresIn: process.env.JWT_REFRESH_EXPIRATION_TIME,
        },
      ),
    };
  }

  async refresh(
    refreshRequestDto: RefreshRequestDto,
  ): Promise<RefreshResponseDto> {
    const { refreshToken } = refreshRequestDto;
    try {
      const { uuid } = await this.jwtService.verifyAsync(refreshToken, {
        secret: process.env.JWT_REFRESH_SECRET,
      });
      return new RefreshResponseDto({
        accessToken: await this.jwtService.signAsync({ id: uuid }),
      });
    } catch (e) {
      throw new InvalidRefreshTokenException();
    }
  }

  async signin(signinRequestDto: SigninRequestDto): Promise<SigninResponseDto> {
    const { uuid } = signinRequestDto;
    const user = await this.UserModel.findOne({ uuid });
    if (!user) {
      throw new LoginFailException();
    }
    const jwt = await this.getTokens(uuid);
    const profile = new ProfileResponseDto({
      uuid: user.uuid,
      nickname: user.nickname,
      statusMessage: user.statusMessage,
    });

    return new SigninResponseDto({ jwt, profile });
  }
}
