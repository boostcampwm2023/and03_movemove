import { UserInfoDto } from 'src/user/dto/user-info.dto';
import { JwtDto } from './jwt.dto';

export class SignupResponseDto {
  constructor(init: SignupResponseDto) {
    Object.assign(this, init);
  }

  jwt: JwtDto;

  profile: UserInfoDto;
}
