import { getObject } from './getObject';

export async function getBucketImage(
  bucket: string,
  ImageExtension: string,
  uuid: string,
) {
  const bucketImage = ImageExtension
    ? await getObject(bucket, `${uuid}.${ImageExtension}`)
    : null;
  return bucketImage;
}
