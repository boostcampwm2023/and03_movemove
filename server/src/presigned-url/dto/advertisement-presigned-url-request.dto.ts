import { IsNotEmpty, IsOptional } from 'class-validator';

export class AdvertisementPresignedUrlRequestDto {
  /**
   * 특정 광고 이미지의 presigned url만 받고 싶은 경우
   * @example 'test.webp'
   */
  @IsOptional()
  @IsNotEmpty()
  name?: string;
}
