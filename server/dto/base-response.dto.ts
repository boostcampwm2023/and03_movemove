import { IsNotEmpty } from "class-validator";

export interface BaseResponseDto{
    statusCode:number;
    message:string;
    data: object;
}
