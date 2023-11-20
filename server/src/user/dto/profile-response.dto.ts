import { IsNotEmpty, IsUrl } from 'class-validator';

export class ProfileResponseDto {
  /**
   * 유저 프로필 URL
   * @example https://movemove/users/profile/:id
   */
  @IsUrl()
  profileImage: string;

  /**
   * 유저 닉네임
   * @example '페이커'
   */
  @IsNotEmpty()
  nickname: string;

  /**
   * 한줄 소개
   * @example '역대 최연소 우승자, 역대 최고령 우승자'
   */
  @IsNotEmpty()
  statusMessage: string;
}
