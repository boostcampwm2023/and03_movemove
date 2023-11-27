import { PickType } from '@nestjs/swagger';
import { VideoResponseDto } from './video-response.dto';

export class VideoSummaryResponseDto extends PickType(VideoResponseDto, [
  '_id',
  'title',
  'content',
  'category',
]) {
  constructor(init: VideoSummaryResponseDto) {
    super();
    Object.assign(this, init);
  }
}
