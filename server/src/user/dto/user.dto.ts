import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsUUID } from 'class-validator';

export class UserDto {
  @ApiProperty({
    description: 'UUID',
    format: 'uuid',
    example: '550e8400-e29b-41d4-a716-446655440000',
  })
  @IsUUID()
  uuid: string;

  @ApiProperty({
    description: '유저 프로필 사진',
    type: 'string',
    format: 'binary',
  })
  // TODO file 유효성검사
  profileImage: Express.Multer.File;

  /**
   * 유저 닉네임
   * @example '페이커'
   */
  @IsNotEmpty()
  nickname: string;

  /**
   * 한줄 소개
   * @example '역대 최연소 우승자, 역대 최고령 우승자'
   */
  @IsNotEmpty()
  statusMessage: string;
}
