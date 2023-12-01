export class ProfileResponseDto {
  /**
   * 닉네임
   * @example 'honux'
   */
  nickname: string;

  /**
   * 상태 메세지
   * @example 'web be 마스터'
   */
  statusMessage: string;

  /**
   * 프로필 이미지 가져오는 presigned url
   * @example 'https://ncloud.com/'
   */
  profileImageUrl: string;
}
