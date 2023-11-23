import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsString, IsUUID } from 'class-validator';

export class UserDto {
  /**
   * 유저 ID
   * @example '550e8400-e29b-41d4-a716-446655440000'
   */
  @IsUUID()
  uuid: string;

  @ApiProperty({
    description: '유저 프로필 사진',
    type: 'string',
    format: 'binary',
    example: '인코딩된 이미지 파일',
  })
  // TODO file 유효성검사
  profileImage?: Express.Multer.File;

  /**
   * 유저 닉네임
   * @example '페이커'
   */
  @IsNotEmpty()
  nickname: string;

  /**
   * 한줄 소개
   * @example 'Unkillable Demon King'
   */
  @IsNotEmpty()
  statusMessage: string;
}
