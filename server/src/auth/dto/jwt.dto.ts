import { IsJWT } from 'class-validator';

export class JwtDto {
  /**
   * @example 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiMTIzNDU2Nzg5MCIsImlzUmVmcmVzaCI6ZmFsc2V9.h3z-CqF_57tdGn18inYCStZFWT9BcN9Ng67_Ir8ojq0'
   */
  @IsJWT()
  accessToken: string;

  /**
   * @example 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiMTIzNDU2Nzg5MCIsImlzUmVmcmVzaCI6dHJ1ZX0._BeS7-Egk30iN8tGkQK-LOkq4rGiRQYnQz_SpSO6lLU'
   */
  @IsJWT()
  refreshToken: string;
}
