import { VideoListResponseDto } from './video-list-response.dto';

export class RandomVideoResponseDto extends VideoListResponseDto {
  /**
   * @example https://server_ip/videos/random?limit=&category=&seed=
   */
  next: string;
}
