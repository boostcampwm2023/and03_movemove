import { OmitType } from '@nestjs/swagger';
import { UserDto } from './user.dto';

export class ProfileResponseDto extends OmitType(UserDto, [
  'profileImage',
] as const) {
  constructor(init: ProfileResponseDto) {
    super();
    Object.assign(this, init);
  }
}
