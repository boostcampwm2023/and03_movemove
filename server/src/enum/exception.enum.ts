enum ErrorCodeEnum {
  UserConflict = 3001,
  BadRequest = 2000,
  LoginFail = 1000,
}

const ErrorMessage = {
  [ErrorCodeEnum.UserConflict]: '이미 가입된 회원입니다',
  [ErrorCodeEnum.BadRequest]: '잘못된 요청 형식입니다',
  [ErrorCodeEnum.LoginFail]: '잘못된 요청 형식입니다',
};

export { ErrorCodeEnum, ErrorMessage };
