import { OmitType } from '@nestjs/swagger';
import { UserDto } from './user.dto';

export class UserInfoDto extends OmitType(UserDto, ['profileImage'] as const) {
  constructor(init: UserInfoDto) {
    super();
    Object.assign(this, init);
  }
}
