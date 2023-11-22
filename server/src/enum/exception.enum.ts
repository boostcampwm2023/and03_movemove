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
  [ErrorCodeEnum.OAuthFailed]: '소셜 Token 인증 오류',
  [ErrorCodeEnum.InvalidRefreshToken]: '유효하지 않은 RefreshToken',
};

export { ErrorCodeEnum, ErrorMessage };
