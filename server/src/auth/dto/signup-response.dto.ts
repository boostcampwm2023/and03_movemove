import { BaseResponseDto } from "dto/base-response.dto";
import { JwtResponseDto } from "./jwt-response.dto";
import { ProfileResponseDto } from "src/user/dto/profile-response.dto";

export class SignupResponseDto implements BaseResponseDto{
    /**
     * @example 201
     */
    statusCode: number;
    /**
     * @example '회원가입 성공'
     */
    message: string;
    data:{
        jwt: JwtResponseDto,
        profile:ProfileResponseDto
    }
}
