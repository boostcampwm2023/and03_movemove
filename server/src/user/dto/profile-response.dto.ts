import { OmitType } from '@nestjs/swagger';
import { IsUrl } from 'class-validator';
import { UserDto } from './user.dto';

export class ProfileResponseDto extends OmitType(UserDto, [
  'profileImage',
] as const) {
  /**
   * 유저 프로필 URL
   * @example https://movemove/users/profile/:id
   */
  @IsUrl()
  profileImage: string;

  constructor(init: Omit<ProfileResponseDto, 'profileImage'>) {
    super();
    Object.assign(this, init);
    this.profileImage = `/users/profile/${this.uuid}`;
  }
}
