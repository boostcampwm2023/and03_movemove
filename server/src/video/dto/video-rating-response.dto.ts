import { VideoRatingDTO } from './video-rating.dto';

export class VideoRatingResponseDTO extends VideoRatingDTO {
  /**
   * 비디오 ID
   * @example "655f3ee338cddcbadc60cc8a"
   */
  videoId: string;
}
