import { IsUUID } from 'class-validator';

export class ProfilePresignedUrlRequestDto {
  /**
   * user uuid
   */
  @IsUUID()
  uuid: string;

  /**
   * 프로필 이미지 확장자
   * @example 'webp'
   */
  profileExtension: string;
}
