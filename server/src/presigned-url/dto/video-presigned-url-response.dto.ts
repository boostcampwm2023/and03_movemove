import { Types } from 'mongoose';

export class VideoPresignedUrlResponseDto {
  /**
   * 비디오 ID
   * @example '6567eb9aa1efacad06e24b81'
   */
  videoId: Types.ObjectId;

  /**
   * 비디오 업로드 presigned url
   * @example 'https://kr.object.ncloudstorage.com'
   */
  videoUrl: string;

  /**
   * 썸네일 업로드 presigned url
   * @example 'https://kr.object.ncloudstorage.com'
   */
  thumbnailUrl: string;
}
