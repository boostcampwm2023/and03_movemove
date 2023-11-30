import { VideoListResponseDto } from './video-list-response.dto';

export class RandomVideoResponseDto extends VideoListResponseDto {
  /**
   * @example 123456
   */
  seed: number;
}
