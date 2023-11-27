import { UploaderResponseDto } from 'src/user/dto/uploader-response.dto';
import { VideoResponseDto } from 'src/video/dto/video-response.dto';

export class RatedVideoInfoDto {
  constructor(init: RatedVideoInfoDto) {
    Object.assign(this, init);
  }

  video: VideoResponseDto;

  uploader: UploaderResponseDto;

  ratedAt: Date;
}
