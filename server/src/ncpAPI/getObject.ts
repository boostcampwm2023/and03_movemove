import axios from 'axios';
import { arrayBuffer } from 'stream/consumers';
import { getTimeStamp, getAuthorization } from './common';

export const getObject = async (bucketName: string, objectName: string) => {
  const endPoint = 'https://kr.object.ncloudstorage.com';
  const canonicalURI = `/${bucketName}/${objectName}`;
  const apiUrl = `${endPoint}${canonicalURI}`;

  const timeStamp = getTimeStamp();
  const defaultHeaders = {
    host: 'kr.object.ncloudstorage.com',
    'x-amz-content-sha256': 'UNSIGNED-PAYLOAD',
    'x-amz-date': timeStamp,
  };

  const method = 'GET';
  return axios
    .get(apiUrl, {
      headers: {
        Authorization: getAuthorization(
          method,
          canonicalURI,
          defaultHeaders,
          timeStamp,
        ),
        ...defaultHeaders,
      },
      responseType: 'text',
      responseEncoding: 'base64',
    })
    .then((response) => response.data);
};
