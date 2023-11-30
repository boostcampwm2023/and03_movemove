/* eslint-disable max-classes-per-file */
import { ApiProperty } from '@nestjs/swagger';

class PresignedUrlDto {
  /**
   * 파일 이름
   * @example 'test.webp'
   */
  name: string;

  /**
   * 파일을 가져올 수 있는 presigned url
   * @example "https://kr.object.ncloudstorage.com/"
   */
  url: string;
}

export class AdvertisementPresignedUrlResponseDto {
  @ApiProperty({ type: [PresignedUrlDto] })
  advertisements: [PresignedUrlDto];
}
