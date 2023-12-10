/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/naming-convention */
/* eslint-disable @typescript-eslint/no-var-requires */
const ffmpeg = require('fluent-ffmpeg');
const fs = require('fs');
const axios = require('axios');

const apiUrl = '비밀';

async function main(params) {
  const results = [];
  const { videoUrl, object_name, greenEyeSecret, accessToken } = params;
  await createScreanshots(videoUrl);
  const files = Array.from(
    { length: 4 },
    (_, k) => `${object_name}_${k + 1}.png`,
  );
  // eslint-disable-next-line no-restricted-syntax
  for (const file of files) {
    const data = fs.readFileSync(file);
    const imageData = data.toString('base64');
    // eslint-disable-next-line no-await-in-loop
    const result = await greenEye(file, file, imageData, greenEyeSecret);
    results.push(result);
    fs.unlinkSync(file);
    // eslint-disable-next-line no-await-in-loop
    await new Promise((resolve) => {
      setTimeout(resolve, 1000);
    });
  }
  console.log(results);
  if (checkHarmful(results)) {
    axios.delete(`http://223.130.136.106/videos/${object_name}`, {
      headers: { Authorization: `Bearer ${accessToken}` },
    });
  }
}

const createScreanshots = async (videoUrl) => {
  const timestamps = Array.from(
    { length: 4 },
    () => `${Math.floor(Math.random() * 101)}%`,
  );
  console.log(timestamps);
  return new Promise((resolve) => {
    ffmpeg(videoUrl)
      .screenshots({
        timestamps,
        filename: `%b.png`,
        folder: './',
      })
      .on('end', () => {
        resolve();
      });
  });
};

const greenEye = (requestId, imageName, imageData, greenEyeSecret) => {
  const headers = {
    'X-GREEN-EYE-SECRET': greenEyeSecret,
    'Content-Type': 'application/json',
  };

  const data = {
    version: 'V1',
    requestId,
    timestamp: Number(new Date()),
    images: [
      {
        name: imageName,
        data: imageData,
      },
    ],
  };

  return axios
    .post(apiUrl, data, {
      headers,
    })
    .then((response) => response.data.images.pop().result);
};

const checkHarmful = (results) => {
  return results.some((result) => result.normal.confidence < 0.5);
};
