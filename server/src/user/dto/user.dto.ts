import { IsNotEmpty, IsUUID } from 'class-validator';

export class UserDto {
  /**
   * 유저 ID
   * @example '550e8400-e29b-41d4-a716-446655440000'
   */
  @IsUUID()
  uuid: string;

  /**
   * 프로필 이미지 확장자
   * @example 'webp'
   */
  profileImageExtension?: string | null = null;

  /**
   * 유저 닉네임
   * @example '페이커'
   */
  @IsNotEmpty()
  nickname: string;

  /**
   * 한줄 소개
   * @example 'Unkillable Demon King'
   */
  @IsNotEmpty()
  statusMessage: string;
}
