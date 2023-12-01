import { IsNotEmpty } from 'class-validator';

export class ProfilePresignedUrlRequestDto {
  /**
   * 프로필 이미지 확장자
   * @example 'webp'
   */
  @IsNotEmpty()
  profileExtension: string;
}
