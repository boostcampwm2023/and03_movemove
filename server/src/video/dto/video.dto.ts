import { IsNotEmpty, IsNumber, IsString } from 'class-validator';

export class VideoRatingDTO {
  @IsNumber()
  rating: number;

  @IsString()
  reason: string;
}

export class VideoDto {
  @IsNotEmpty()
  title: string;

  @IsNotEmpty()
  content: string;

  video: string;
  category: string;
  thumbnail: string;
}
