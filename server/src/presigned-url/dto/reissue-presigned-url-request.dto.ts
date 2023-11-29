import { IsEnum } from 'class-validator';

enum TypeEnum {
  thumbnail = 'thumbnail',
  profile = 'profile',
}

export class ReissuePresignedUrlRequestDto {
  /**
   * 요청하는 이미지 종류
   * @example 'thumbnail'
   */
  @IsEnum(TypeEnum)
  type: TypeEnum;

  /**
   * 이미지 확장자
   * @example 'webp'
   */
  extension: string;
}
