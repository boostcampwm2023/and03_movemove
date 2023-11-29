import {
  IsEnum,
  IsInt,
  IsNumber,
  IsOptional,
  IsPositive,
} from 'class-validator';
import { CategoryEnum } from '../enum/category.enum';

export class RandomVideoQueryDto {
  /**
   * 요청하는 비디오 수
   */
  @IsInt()
  @IsPositive()
  limit: number;

  /**
   * 비디오 카테고리
   * @example '챌린지'
   */
  @IsEnum(CategoryEnum)
  category: CategoryEnum;

  /**
   * 해당 시드로 시청된 영상은 제외하여 응답
   * @example 123456
   */
  @IsOptional()
  @IsNumber()
  seed?: number;
}
