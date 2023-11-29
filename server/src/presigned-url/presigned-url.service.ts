import { Injectable } from '@nestjs/common';
import { createPresignedUrl } from 'src/ncpAPI/presignedURL';
import { listObjects } from 'src/ncpAPI/listObjects';
import * as _ from 'lodash';
import { xml2js } from 'xml-js';
import { Types } from 'mongoose';

@Injectable()
export class PresignedUrlService {
  async getAdvertisementPresignedUrl(adName: string) {
    if (adName)
      return createPresignedUrl(
        process.env.ADVERTISEMENT_BUCKET,
        adName,
        'GET',
      );

    const xmlData = await listObjects(process.env.ADVERTISEMENT_BUCKET);
    const jsonData: any = xml2js(xmlData, { compact: true });

    const adList = _.map(jsonData.ListBucketResult.Contents, 'Key._text');
    const advertisements = await Promise.all(
      adList.map(async (advertisement: string) => {
        return createPresignedUrl(
          process.env.ADVERTISEMENT_BUCKET,
          advertisement,
          'GET',
        );
      }),
    );
    return { advertisements };
  }

  async putProfilePresignedUrl({ uuid, profileExtension }) {
    const objectName = `${uuid}.${profileExtension}`;
    const presignedUrl = (
      await createPresignedUrl(process.env.PROFILE_BUCKET, objectName, 'PUT')
    ).url;
    return { presignedUrl };
  }

  async putVideoPresignedUrl({ videoExtension, thumbnailExtension }) {
    const videoId = new Types.ObjectId();
    const [videoUrl, thumbnailUrl] = await Promise.all([
      (
        await createPresignedUrl(
          process.env.INPUT_BUCKET,
          `${videoId}.${videoExtension}`,
          'PUT',
        )
      ).url,
      (
        await createPresignedUrl(
          process.env.THUMBNAIL_BUCKET,
          `${videoId}.${thumbnailExtension}`,
          'PUT',
        )
      ).url,
    ]);
    return { videoId, videoUrl, thumbnailUrl };
  }

  async getImagePresignedUrl(id: string, { type, extension }) {
    // TODO 업로드 된 이미지인지 확인
    const bucketName = {
      thumbnail: process.env.THUMBNAIL_BUCKET,
      profile: process.env.PROFILE_BUCKET,
    }[type];
    const objectName = `${id}.${extension}`;
    const presignedUrl = (
      await createPresignedUrl(bucketName, objectName, 'GET')
    ).url;
    return { presignedUrl };
  }
}
