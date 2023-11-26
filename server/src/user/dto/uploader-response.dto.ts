import { IsUUID } from 'class-validator';

export class UploaderResponseDto {
  /**
   * 업로더 uuid
   * @example "550e5300-e23b-12d3-a542-634585671242"
   */
  @IsUUID()
  uuid: string;

  /**
   * 업로더 닉네임
   * @example "honux"
   */
  nickname: string;

  /**
   * 업로더 상태 메세지
   * @example "백엔드 마스터"
   */
  statusMessage: string;

  /**
   * 업로더 이미지
   * @example "�PNG\r\n\u001a\n\u0000\u0000\u0000\"
   */
  profileImage: string;
}
