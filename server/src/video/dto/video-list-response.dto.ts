import { ApiProperty } from '@nestjs/swagger';
import { VideoInfoDto } from './video-info.dto';

export class VideoListResponseDto {
  constructor(init: VideoListResponseDto) {
    Object.assign(this, init);
  }

  @ApiProperty({ type: [VideoInfoDto] })
  videos: [VideoInfoDto];
}
