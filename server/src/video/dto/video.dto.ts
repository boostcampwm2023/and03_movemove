import { IsEnum, IsNotEmpty } from 'class-validator';
import { CategoryEnum } from '../enum/category.enum';

export class VideoDto {
  /**
   * 비디오 제목
   * @example 제목입니다
   */
  @IsNotEmpty()
  title: string;

  /**
   * 비디오 내용
   * @example 내용입니다
   */
  @IsNotEmpty()
  content: string;

  /**
   * 비디오 카테고리
   * @example '챌린지'
   */
  @IsEnum(CategoryEnum)
  category: CategoryEnum;

  /**
   * 비디오 확장자
   * @example 'mp4'
   */
  videoExtension: string;

  /**
   * 썸네일 이미지 확장자
   * @example 'webp'
   */
  thumbnailExtension: string;
}
