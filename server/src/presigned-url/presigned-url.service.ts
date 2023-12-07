import { Injectable } from '@nestjs/common';
import { createPresignedUrl } from 'src/ncpAPI/presignedURL';
import { listObjects } from 'src/ncpAPI/listObjects';
import { Types } from 'mongoose';
import { AdvertisementPresignedUrlResponseDto } from './dto/advertisement-presigned-url-response.dto';
import { PresignedUrlResponseDto } from './dto/presigned-url-response.dto';
import { VideoPresignedUrlResponseDto } from './dto/video-presigned-url-response.dto';

@Injectable()
export class PresignedUrlService {
  async getAdvertisementPresignedUrl(
    adName: string,
  ): Promise<AdvertisementPresignedUrlResponseDto> {
    if (adName)
      return {
        advertisements: [
          {
            name: adName,
            url: await createPresignedUrl(
              process.env.ADVERTISEMENT_BUCKET,
              adName,
              'GET',
            ),
          },
        ],
      };

    const adList = await listObjects(process.env.ADVERTISEMENT_BUCKET);
    const advertisements = await Promise.all(
      adList.map(async (advertisement: string) => {
        return {
          name: advertisement,
          url: await createPresignedUrl(
            process.env.ADVERTISEMENT_BUCKET,
            advertisement,
            'GET',
          ),
        };
      }),
    );
    return { advertisements };
  }

  async putProfilePresignedUrl(
    uuid: string,
    profileExtension: string,
  ): Promise<PresignedUrlResponseDto> {
    const objectName = `${uuid}.${profileExtension}`;
    const presignedUrl = await createPresignedUrl(
      process.env.PROFILE_BUCKET,
      objectName,
      'PUT',
    );
    return { presignedUrl };
  }

  async putVideoPresignedUrl({
    videoExtension,
    thumbnailExtension,
  }): Promise<VideoPresignedUrlResponseDto> {
    const videoId = new Types.ObjectId();
    const [videoUrl, thumbnailUrl] = await Promise.all([
      await createPresignedUrl(
        process.env.INPUT_BUCKET,
        `${videoId}.${videoExtension}`,
        'PUT',
      ),
      await createPresignedUrl(
        process.env.THUMBNAIL_BUCKET,
        `${videoId}.${thumbnailExtension}`,
        'PUT',
      ),
    ]);
    return { videoId, videoUrl, thumbnailUrl };
  }

  async getImagePresignedUrl(
    id: string,
    { type, extension },
  ): Promise<PresignedUrlResponseDto> {
    // TODO 업로드 된 이미지인지 확인
    const bucketName = {
      thumbnail: process.env.THUMBNAIL_BUCKET,
      profile: process.env.PROFILE_BUCKET,
    }[type];
    const objectName = `${id}.${extension}`;
    const presignedUrl = await createPresignedUrl(
      bucketName,
      objectName,
      'GET',
    );
    return { presignedUrl };
  }
}
