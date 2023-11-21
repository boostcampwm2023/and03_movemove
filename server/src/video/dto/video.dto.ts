import { ApiProperty } from '@nestjs/swagger';
import { IsEnum, IsNotEmpty } from 'class-validator';
import { CategoryEnum } from '../enum/category.enum';

export class VideoDto {
  /**
   * 비디오 제목
   */
  @IsNotEmpty()
  title: string;

  /**
   * 비디오 내용
   */
  @IsNotEmpty()
  content: string;

  @ApiProperty({
    description: '비디오 파일',
    type: 'string',
    format: 'binary',
    required: true,
  })
  video: Express.Multer.File;

  /**
   * 비디오 카테고리
   * @example '챌린지'
   */
  @IsEnum(CategoryEnum)
  category: CategoryEnum;

  @ApiProperty({
    description: '비디오 썸네일 이미지',
    type: 'string',
    format: 'binary',
    required: true,
  })
  thumbnail: Express.Multer.File;
}
