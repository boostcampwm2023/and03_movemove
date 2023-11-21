import { ProfileResponseDto } from 'src/user/dto/profile-response.dto';
import { JwtResponseDto } from './jwt-response.dto';

export class SigninResponseDto {
  constructor(init: SigninResponseDto) {
    Object.assign(this, init);
  }

  jwt: JwtResponseDto;

  profile: ProfileResponseDto;
}
