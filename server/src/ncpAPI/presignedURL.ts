import { HttpRequest } from '@smithy/protocol-http';
import { S3RequestPresigner } from '@aws-sdk/s3-request-presigner';
import { parseUrl } from '@smithy/url-parser';
import { formatUrl } from '@aws-sdk/util-format-url';
import { Hash } from '@smithy/hash-node';

export const createPresignedUrl = async (
  bucketName: string,
  objectName: string,
  method: string,
) => {
  const region = 'kr-standard';
  const endPoint = 'https://kr.object.ncloudstorage.com';
  const canonicalURI = `/${bucketName}/${objectName}`;
  const apiUrl = `${endPoint}${canonicalURI}`;
  const url = parseUrl(apiUrl);

  const presigner = new S3RequestPresigner({
    credentials: {
      accessKeyId: process.env.ACCESS_KEY,
      secretAccessKey: process.env.SECRET_KEY,
    },
    region,
    sha256: Hash.bind(null, 'sha256'),
  });

  const signedUrlObject = await presigner.presign(
    new HttpRequest({ ...url, method }),
    { expiresIn: 10000 },
  );
  return formatUrl(signedUrlObject);
};
