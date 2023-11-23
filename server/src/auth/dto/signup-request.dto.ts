import { IsNotEmpty } from 'class-validator';
import { UserDto } from 'src/user/dto/user.dto';

export class SignupRequestDto extends UserDto {
  /**
   * 소셜 accessToken
   * @example '1/fFAGRNJru1FTz70BzhT3Zg'
   */
  @IsNotEmpty()
  accessToken: string;
}
