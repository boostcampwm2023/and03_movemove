import { IsEnum, IsNotEmpty } from 'class-validator';
import { UserDto } from 'src/user/dto/user.dto';

export enum PlatformEnum {
  GOOGLE = 'GOOGLE',
  KAKAO = 'KAKAO',
}

export class SignupRequestDto extends UserDto {
  /**
   * 소셜 idToken
   * @example '1/fFAGRNJru1FTz70BzhT3Zg'
   */
  @IsNotEmpty()
  idToken: string;

  /**
   * 소셜 platform 종류
   * @example 'GOOGLE'
   */
  @IsEnum(PlatformEnum)
  platform: PlatformEnum;
}
