import { VideoResponseDto } from 'src/video/dto/video-response.dto';
import { ApiProperty } from '@nestjs/swagger';
import { UploaderResponseDto } from 'src/user/dto/uploader-response.dto';

export class RandomVideoResponseDto {
  constructor(init: RandomVideoResponseDto) {
    Object.assign(this, init);
  }

  video: VideoResponseDto;

  uploader: UploaderResponseDto;
}
