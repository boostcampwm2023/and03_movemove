import { VideoResponseDto } from 'src/video/dto/video-response.dto';
import { ApiProperty } from '@nestjs/swagger';
import { UploaderResponseDto } from './uploader-response.dto';

export class UploadedVideoResponseDto {
  constructor(init: UploadedVideoResponseDto) {
    Object.assign(this, init);
  }

  uploader: UploaderResponseDto;

  @ApiProperty({ type: [VideoResponseDto] })
  videos: VideoResponseDto[];
}
