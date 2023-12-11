import { IsEnum, IsNotEmpty } from 'class-validator';
import { UserDto } from 'src/user/dto/user.dto';

enum PlatformEnum {
  GOOGLE = 'GOOGLE',
  KAKAO = 'KAKAO',
}

export class SignupRequestDto extends UserDto {
  /**
   * 소셜 idToken
   * @example '1234567890'
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
