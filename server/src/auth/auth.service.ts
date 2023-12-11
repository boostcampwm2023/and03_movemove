import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { UserConflictException } from 'src/exceptions/conflict.exception';
import { User, UserDocument } from 'src/user/schemas/user.schema';
import { JwtService } from '@nestjs/jwt';
import { LoginFailException } from 'src/exceptions/login-fail.exception';
import { InvalidRefreshTokenException } from 'src/exceptions/invalid-refresh-token.exception';
import { UserInfoDto } from 'src/user/dto/user-info.dto';
import { checkUpload } from 'src/ncpAPI/listObjects';
import { ProfileUploadRequiredException } from 'src/exceptions/profile-upload-required-exception';
import { OAuth2Client } from 'google-auth-library';
import { InvalidGoogldIdTokenException } from 'src/exceptions/invalid-google-idToken.exception';
import { InconsistentGoogldUuidException } from 'src/exceptions/inconsistent-google-uuid.exception';
import { SignupRequestDto } from './dto/signup-request.dto';
import { JwtDto } from './dto/jwt.dto';
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

  async checkUserConflict(uuid: string): Promise<void> {
    if (await this.UserModel.findOne({ uuid })) {
      throw new UserConflictException();
    }
  }

  async create(signupRequestDto: SignupRequestDto): Promise<SignupResponseDto> {
    const { uuid, profileImageExtension, platform, idToken } = signupRequestDto;
    await this.verifyUuid(platform, idToken, uuid);
    await this.checkUserConflict(uuid);
    if (
      profileImageExtension &&
      !(await checkUpload(
        process.env.PROFILE_BUCKET,
        `${uuid}.${profileImageExtension}`,
      ))
    ) {
      throw new ProfileUploadRequiredException();
    }

    const newUser = new this.UserModel({
      ...signupRequestDto,
    });
    newUser.save();
    return this.getLoginInfo(newUser).then(
      (loginInfo) => new SignupResponseDto(loginInfo),
    );
  }

  async getTokens(uuid: string): Promise<JwtDto> {
    return {
      accessToken: await this.jwtService.signAsync({ id: uuid }),
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

  async getLoginInfo(
    user: UserDocument,
  ): Promise<{ jwt: JwtDto; profile: UserInfoDto }> {
    const jwt = await this.getTokens(user.uuid);
    const profile = new UserInfoDto({
      uuid: user.uuid,
      nickname: user.nickname,
      statusMessage: user.statusMessage,
    });
    return { jwt, profile };
  }

  async signin(signinRequestDto: SigninRequestDto): Promise<SigninResponseDto> {
    const { uuid, platform, idToken } = signinRequestDto;
    await this.verifyUuid(platform, idToken, uuid);
    const user = await this.UserModel.findOne({ uuid });
    if (!user) {
      throw new LoginFailException();
    }
    return this.getLoginInfo(user).then(
      (loginInfo) => new SigninResponseDto(loginInfo),
    );
  }

  async verifyUuid(platform: string, idToken: string, uuid: string) {
    switch (platform) {
      case 'GOOGLE':
        if (uuid !== (await this.verifyGoogleIdToken(idToken)))
          throw new InconsistentGoogldUuidException();
        break;
      default:
    }
  }

  async verifyGoogleIdToken(idToken: string) {
    const client = new OAuth2Client();
    console.log(idToken);
    try {
      const ticket = await client.verifyIdToken({
        idToken,
        audience: process.env.GOOGLE_CLIENT_ID,
      });
      const payload = ticket.getPayload();
      const userid = payload.sub;
      const uuid = this.formatAsUUID(
        Number(userid.substring(0, 10)),
        Number(userid.substring(10)),
      );
      return uuid;
    } catch (err) {
      throw new InvalidGoogldIdTokenException();
    }
  }
}
