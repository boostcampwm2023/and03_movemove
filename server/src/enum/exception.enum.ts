enum ErrorCodeEnum {
  UserConflict = 3001,
  BadRequest = 2000,
  LoginFail = 1000,
  TokenExpired = 1001,
  InvalidToken = 1002,
  BadTokenFormat = 1003,
  OAuthFailed = 1004,
  InvalidRefreshToken = 1005,
}

const ErrorMessage = {
  [ErrorCodeEnum.UserConflict]: '이미 가입된 회원',
  [ErrorCodeEnum.BadRequest]: '잘못된 요청 형식',
  [ErrorCodeEnum.LoginFail]: '가입되지 않은 회원',
  [ErrorCodeEnum.TokenExpired]: 'AccessToken 만료',
  [ErrorCodeEnum.InvalidToken]: 'AccessToken 검증 오류',
  [ErrorCodeEnum.OAuthFailed]: 'userID 검증 오류',
  [ErrorCodeEnum.InvalidRefreshToken]: '유효하지 않은 RefreshToken',
};

const ErrorDescription = {
  [ErrorCodeEnum.UserConflict]: '회원 userID가 중복됨',
  [ErrorCodeEnum.BadRequest]: 'Format 오류, 관리자에게 문의',
  [ErrorCodeEnum.LoginFail]: 'userID를 가진 유저가 없음',
  [ErrorCodeEnum.TokenExpired]: 'AccessToken 재발급 필요',
  [ErrorCodeEnum.OAuthFailed]: '소셜 토큰이 유효하지 않거나 userID가 다름',
  [ErrorCodeEnum.InvalidToken]: 'AccessToken이 위변조됨',
  [ErrorCodeEnum.InvalidRefreshToken]: 'RefreshToken이 만료되었거나 위변조됨',
};
export { ErrorCodeEnum, ErrorMessage, ErrorDescription };
