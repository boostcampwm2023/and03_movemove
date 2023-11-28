import { IsUUID } from 'class-validator';

export class RaterResponseDto {
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
}
