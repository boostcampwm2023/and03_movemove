import axios from 'axios';

export const greenEye = (requestId: string, imageUrl: string) => {
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
        name: 'demo',
        url: imageUrl,
      },
    ],
  };

  return axios
    .post(apiUrl, data, {
      headers,
    })
    .then((response) => response.data.images.pop().result)
    .catch((error) => error);
};
