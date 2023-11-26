import { PickType } from '@nestjs/swagger';
import { UserDto } from './user.dto';

export class ProfileDto extends PickType(UserDto, ['profileImage'] as const) {
  constructor(init: ProfileDto) {
    super();
    Object.assign(this, init);
  }

  /**
   * 유저 닉네임
   * @example '페이커'
   */
  nickname?: string;

  /**
   * 한줄 소개
   * @example 'Unkillable Demon King'
   */
  statusMessage?: string;
}
