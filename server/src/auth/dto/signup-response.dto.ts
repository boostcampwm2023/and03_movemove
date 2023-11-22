import { ProfileResponseDto } from 'src/user/dto/profile-response.dto';
import { JwtResponseDto } from './jwt-response.dto';

export class SignupResponseDto {
  constructor(init: SignupResponseDto) {
    Object.assign(this, init);
  }

  jwt: JwtResponseDto;

  profile: ProfileResponseDto;
}
