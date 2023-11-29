import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model, Types } from 'mongoose';
import { UserConflictException } from 'src/exceptions/conflict.exception';
import { User, UserDocument } from 'src/user/schemas/user.schema';
import { JwtService } from '@nestjs/jwt';
import { LoginFailException } from 'src/exceptions/login-fail.exception';
import { InvalidRefreshTokenException } from 'src/exceptions/invalid-refresh-token.exception';
import { UserInfoDto } from 'src/user/dto/user-info.dto';
import { getPresignedUrl } from 'src/ncpAPI/presignedURL';
import * as _ from 'lodash';
import { listObjects } from 'src/ncpAPI/listObjects';
import { xml2js } from 'xml-js';
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

  async create(signupRequestDto: SignupRequestDto): Promise<SignupResponseDto> {
    const { uuid } = signupRequestDto;
    if (await this.UserModel.findOne({ uuid })) {
      throw new UserConflictException();
    }
    const profileImageExtension = signupRequestDto.profileExtension;
    if (profileImageExtension) {
      // TODO 프로필 이미지 업로드 됐는지 확인
    }

    const newUser = new this.UserModel({
      ...signupRequestDto,
      profileImageExtension,
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
    const { uuid } = signinRequestDto;
    const user = await this.UserModel.findOne({ uuid });
    if (!user) {
      throw new LoginFailException();
    }
    return this.getLoginInfo(user).then(
      (loginInfo) => new SigninResponseDto(loginInfo),
    );
  }

  async getAdvertisementPresignedUrl(adName: string) {
    if (adName)
      return getPresignedUrl(process.env.ADVERTISEMENT_BUCKET, adName, 'GET');

    const xmlData = await listObjects(process.env.ADVERTISEMENT_BUCKET);
    const jsonData: any = xml2js(xmlData, { compact: true });

    const adList = _.map(jsonData.ListBucketResult.Contents, 'Key._text');
    const advertisements = await Promise.all(
      adList.map(async (advertisement: string) => {
        return getPresignedUrl(
          process.env.ADVERTISEMENT_BUCKET,
          advertisement,
          'GET',
        );
      }),
    );
    return { advertisements };
  }

  async getProfilePresignedUrl({ uuid, profileExtension }) {
    const objectName = `${uuid}.${profileExtension}`;
    const presignedUrl = (
      await getPresignedUrl(process.env.PROFILE_BUCKET, objectName, 'PUT')
    ).url;
    return { presignedUrl };
  }

  async getVideoPresignedUrl({ videoExtension, thumbnailExtension }) {
    const videoId = new Types.ObjectId();
    const [videoUrl, thumbnailUrl] = await Promise.all([
      (
        await getPresignedUrl(
          process.env.INPUT_BUCKET,
          `${videoId}.${videoExtension}`,
          'PUT',
        )
      ).url,
      (
        await getPresignedUrl(
          process.env.THUMBNAIL_BUCKET,
          `${videoId}.${thumbnailExtension}`,
          'PUT',
        )
      ).url,
    ]);
    return { videoId, videoUrl, thumbnailUrl };
  }
}
