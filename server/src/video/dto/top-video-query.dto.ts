import { IsEnum } from 'class-validator';
import { CategoryEnum } from '../enum/category.enum';

export class TopVideoQueryDto {
  /**
   * 비디오 카테고리
   * @example '챌린지'
   */
  @IsEnum(CategoryEnum)
  category: CategoryEnum;
}
