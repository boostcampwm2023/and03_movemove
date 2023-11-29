import { RatedVideoInfoDto } from 'src/video/dto/rated-video-info.dto';
import { RaterResponseDto } from './rater-response.dto';

export class RatedVideoResponseDto {
  constructor(init: RatedVideoResponseDto) {
    Object.assign(this, init);
  }

  rater: RaterResponseDto;

  videos: RatedVideoInfoDto[];
}
