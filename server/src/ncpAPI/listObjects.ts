import axios from 'axios';
import { getTimeStamp, getAuthorization } from './common';

// TODO CanonicalQueryString 추가해서 개수 제한
export const listObjects = async (bucketName: string) => {
  const endPoint = 'https://kr.object.ncloudstorage.com';
  const canonicalURI = `/${bucketName}`;
  const apiUrl = `${endPoint}${canonicalURI}`;

  const timeStamp = getTimeStamp();
  const defaultHeaders = {
    accept: 'application/json',
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
    })
    .then((response) => {
      return response.data;
    });
};
