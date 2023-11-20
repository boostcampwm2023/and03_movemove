import { IsNotEmpty } from 'class-validator';

export class VideoDto {
  @IsNotEmpty()
  title: string;

  @IsNotEmpty()
  content: string;

  video: string;

  category: string;

  thumbnail: string;
}
