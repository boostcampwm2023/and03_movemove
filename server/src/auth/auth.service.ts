import { Injectable } from '@nestjs/common';
import { putObject } from 'src/ncpAPI/putObject';
import { UserDto } from 'src/user/dto/user.dto';

@Injectable()
export class AuthService {
  create(userDto: UserDto, profileImage: Express.Multer.File) {
    const extension = profileImage.originalname.split('.').pop();
    putObject(
      process.env.PROFILE_BUCKET,
      `${userDto.uuid}.${extension}`,
      profileImage.buffer,
    );
    return 'asd';
    // return `create user ${userDto}`;
  }

  signin(uuid: string) {
    return `signin user ${uuid}`;
  }
}
