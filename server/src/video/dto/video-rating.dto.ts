import { IsNotEmpty, IsNumber, Max, Min, ValidateIf } from 'class-validator';

export class VideoRatingDTO {
  /**
   * 별점
   * @example 2
   */
  @IsNumber()
  @Min(0)
  @Max(5)
  rating: number;

  /**
   * 사유
   * @example '선정적이에요'
   */
  @ValidateIf((o) => o.rating <= 2)
  @IsNotEmpty()
  reason?: string;
}
