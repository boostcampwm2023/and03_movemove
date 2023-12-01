import { IsNotEmpty } from 'class-validator';

export class VIdeoPresignedUrlRequestDto {
  /**
   * 비디오 확장자
   * @example 'mp4'
   */
  @IsNotEmpty()
  videoExtension: string;

  /**
   * 썸네일 이미지 확장자
   * @example 'webp'
   */
  @IsNotEmpty()
  thumbnailExtension: string;
}
