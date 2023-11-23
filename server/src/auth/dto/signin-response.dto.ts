import { UserInfoDto } from 'src/user/dto/user-info.dto';
import { JwtDto } from './jwt.dto';

export class SigninResponseDto {
  constructor(init: SigninResponseDto) {
    Object.assign(this, init);
  }

  jwt: JwtDto;

  profile: UserInfoDto;
}
