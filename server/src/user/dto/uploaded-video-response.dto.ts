import { VideoResponseDto } from 'src/video/dto/video-response.dto';
import { UploaderResponseDto } from './uploader-response.dto';

export class UploadedVideoResponseDto {
  constructor(init: UploadedVideoResponseDto) {
    Object.assign(this, init);
  }

  uploader: UploaderResponseDto;

  videos: VideoResponseDto[];
}
