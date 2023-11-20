import { IsNumber, IsString } from 'class-validator';

export class VideoRatingDTO {
  @IsNumber()
  rating: number;

  @IsString()
  reason: string;
}
