import { IsJWT } from 'class-validator';

export class RefreshResponseDto {
  constructor(init: RefreshResponseDto) {
    Object.assign(this, init);
  }

  /**
   * @example 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiMTIzNDU2Nzg5MCIsImlzUmVmcmVzaCI6dHJ1ZX0._BeS7-Egk30iN8tGkQK-LOkq4rGiRQYnQz_SpSO6lLU'
   */
  @IsJWT()
  accessToken: string;
}
