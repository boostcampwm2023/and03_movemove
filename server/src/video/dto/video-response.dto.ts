import { IsEnum } from 'class-validator';
import { CategoryEnum } from '../enum/category.enum';

export class VideoResponseDto {
  /**
   * 비디오 ID
   * @example "655f3ee338cddcbadc60cc8a"
   */
  _id: string;

  /**
   * 조회수
   * @example 0
   */
  viewCount: number;

  rating: number;

  /**
   * 카테고리
   * @example "챌린지"
   */
  @IsEnum(CategoryEnum)
  category: CategoryEnum;

  /**
   * 비디오 제목
   * @example "슬릭백"
   */
  title: string;

  /**
   * 비디오 내용
   * @example "슬릭백 원탑"
   */
  content: string;

  /**
   * 비디오 업로드 시간
   * @example "2023-11-23T11:58:31.832Z"
   */
  uploadedAt: Date;

  /**
   * 비디오 manifest URL
   * @example "/master.m3u8"
   */
  manifest: string;

  /**
   * 썸네일 이미지
   * @example "�PNG\r\n\u001a\n\u0000\u0000\u0000\"
   */
  thumbnailImage: string;
}
