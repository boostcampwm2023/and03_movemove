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
import { InvalidTokenException } from 'src/exceptions/invalid-token.exception';
import { OAuthFailedException } from 'src/exceptions/oauth-failed.exception';
import { PlatformEnum, SignupRequestDto } from './dto/signup-request.dto';
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
    const { uuid, profileImageExtension } = signupRequestDto;
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
      profileImageExtension: profileImageExtension ?? null,
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

  verifyKakaoIdToken(uuid: string, idToken: string): Promise<{ id: string }> {
    const tokens = idToken.split('.');
    if (tokens.length !== 3) {
      throw new OAuthFailedException();
    }
    let header;
    let payload;
    let signature;
    try {
      [header, payload, signature] = tokens.map((token) =>
        Buffer.from(token, 'base64').toString('utf-8'),
      );
      header = JSON.parse(header);
      payload = JSON.parse(payload);
      if (
        payload.iss !== process.env.KAKAO_ISS ||
        payload.aud !== process.env.KAKAO_APP_KEY ||
        payload.exp < Date.now() / 1000 ||
        this.verifyUUID(uuid, payload.sub, PlatformEnum.KAKAO)
      ) {
        throw new OAuthFailedException();
      }
    } catch (e) {
      throw new OAuthFailedException();
    }
  }

  formatAsUUID(mostSigBits: number, leastSigBits: number) {
    const most = mostSigBits.toString(16).padStart(16, '0');
    const least = leastSigBits.toString(16).padStart(16, '0');
    return `${most.substring(0, 8)}-${most.substring(8, 12)}-${most.substring(
      12,
    )}-${least.substring(0, 4)}-${least.substring(4)}`;
  }

  verifyUUID(uuid: string, sub: string, platform: PlatformEnum) {
    if (platform === PlatformEnum.KAKAO) {
      const num = Number(sub);
      return uuid === this.formatAsUUID(num, num);
    }
    if (platform === PlatformEnum.GOOGLE) {
      return (
        uuid ===
        this.formatAsUUID(
          Number(sub.substring(0, 10)),
          Number(sub.substring(10)),
        )
      );
    }
    return false;
  }

  async signin(signinRequestDto: SigninRequestDto): Promise<SigninResponseDto> {
    const { uuid } = signinRequestDto;
    const user = await this.UserModel.findOne({ uuid });
    if (!user) {
      throw new LoginFailException();
    }
    return this.getLoginInfo(user).then(
      (loginInfo) => new SigninResponseDto(loginInfo),
    );
  }
}
