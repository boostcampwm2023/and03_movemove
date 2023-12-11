import axios from 'axios';
import { GreenEyeApiFailException } from 'src/exceptions/greeneye-api-fail.exception';

export const greenEye = (
  requestId: string,
  imageName: string,
  imageUrl: string,
) => {
  const apiUrl = process.env.GREENEYE_API_URL;

  const headers = {
    'X-GREEN-EYE-SECRET': process.env.GREENEYE_SECRET,
    'Content-Type': 'application/json',
  };

  const data = {
    version: 'V1',
    requestId,
    timestamp: Number(new Date()),
    images: [
      {
        name: imageName,
        url: imageUrl,
      },
    ],
  };

  return axios
    .post(apiUrl, data, {
      headers,
    })
    .then((response) => response.data.images.pop().result)
    .catch(() => {
      throw new GreenEyeApiFailException();
    });
};
