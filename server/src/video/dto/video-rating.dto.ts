import { IsNumber } from 'class-validator';

export class VideoRatingDTO {
  /**
   * 별점
   * @example 2
   */
  @IsNumber()
  rating: number;

  /**
   * 사유
   * @example '선정적이에요'
   */
  reason?: string;
}
