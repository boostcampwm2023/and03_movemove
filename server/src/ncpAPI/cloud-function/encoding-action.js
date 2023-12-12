/* eslint-disable import/no-unresolved */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/naming-convention */
/* eslint-disable @typescript-eslint/no-var-requires */
const ffmpeg = require('fluent-ffmpeg');
const { PassThrough, Readable } = require('stream');
const fs = require('fs/promises');
const assert = require('assert');
const AWS = require('aws-sdk');

let S3;
const M3U8_START = Buffer.from('#EXTM3U');
const M3U8_END = Buffer.from('#EXT-X-ENDLIST\n');
const defaultPreset = {
  videoCodec: 'libx264',
  audioCodec: 'aac',
  audioChannels: 2,
  audioBitrate: '128k',
  segmentDuration: 5,
  keyFrameInterval: 2,
};

const resolutions = [
  {
    name: '1080p',
    width: 1080,
    height: 1920,
    bitrate: '3000k',
  },
  {
    name: '720p',
    width: 720,
    height: 1280,
    bitrate: '2000k',
  },
];

async function main(params) {
  // set client for Object Storage
  if (!S3) {
    S3 = setS3Client(params.accessKey, params.secretKey);
  }

  const { videoUrl, object_name, outputBucket } = params;
  console.log('params:', params);
  const fileName = object_name.split('.').slice(0, -1).join('.');
  const inputStream = await toStream(videoUrl);
  assert(inputStream);
  console.log('fileName:', fileName);
  try {
    await Promise.all(
      resolutions.map((resolution) =>
        createCommand(inputStream, outputBucket, fileName, resolution),
      ),
    );
    await makeMasterManifest(outputBucket, fileName);
    return { params, upload: true };
  } catch (e) {
    console.log('catch:', e);
    throw e;
  }
}

function setS3Client(accessKey, secretKey) {
  const endpoint = new AWS.Endpoint('https://kr.object.ncloudstorage.com');
  const region = 'kr-standard';
  console.log('--- new object storage client ---');
  return new AWS.S3({
    endpoint,
    region,
    credentials: {
      accessKeyId: accessKey,
      secretAccessKey: secretKey,
    },
  });
}

function toStream(videoUrl) {
  return new Promise((resolve, reject) => {
    const stream = new PassThrough();
    const buffers = [];

    stream.on('data', function (chunk) {
      buffers.push(chunk);
    });
    stream.on('end', function () {
      console.log(`toStream Processing finished !`);
      resolve(Readable.from(Buffer.concat(buffers)));
    });

    stream.on('error', function (err) {
      console.log(`toStream Processing error occured !`);
      reject(err);
    });

    console.log(`toStream Processing started !`);

    ffmpeg(videoUrl)
      .outputFormat('mp4')
      .outputOptions(['-movflags frag_keyframe', '-c copy'])
      .on('error', function (err) {
        console.log(`An error occurred at toStream: ${err.message}`);
      })
      .pipe(stream);
  });
}

function createCommand(inputStream, outputBucket, fileName, resolution) {
  return new Promise((resolve, reject) => {
    ffmpeg(inputStream)
      .inputFormat('mp4')
      .videoCodec(defaultPreset.videoCodec)
      .videoBitrate(`${resolution.bitrate}`)
      .size(`${resolution.width}x${resolution.height}`)
      .audioCodec(defaultPreset.audioCodec)
      .audioBitrate(defaultPreset.audioBitrate)
      .audioChannels(defaultPreset.audioChannels)
      .addOptions([
        '-start_number 0', // start the first .ts segment at index 0
        '-hls_flags split_by_time',
        `-g ${defaultPreset.keyFrameInterval}`, // key frame interval
        `-hls_time ${defaultPreset.segmentDuration}`, // 10 second segment duration
        '-hls_list_size 0', // Maxmimum number of playlist entries (0 means all entries/infinite)
        '-f hls', // HLS format
        `-master_pl_name ${resolution.name}.m3u8`,
      ])
      // .on("error", function (err) {
      //   console.log(`An error occurred ${resolution.name}: `);
      //   reject(err);
      // })
      .pipe(
        createPassThrough(
          outputBucket,
          `${fileName}_${resolution.name}`, // 확장자 trim해서 보냄.
          resolve,
          reject,
        ),
      );
  });
}

function createPassThrough(outputBucket, sbrName, resolve, reject) {
  let segmentNum = 0;
  const buffers = [];
  const stream = new PassThrough();

  stream.on('data', function (chunk) {
    const index = chunk.indexOf(M3U8_START);
    if (index !== -1) {
      if (index > 0) {
        buffers.push(chunk.subarray(0, index)); // ts 파일과 m3u8이 하나의 chunk로 들어오는 경우 ts부분은 buffers에 넣어준다.
      }

      console.log(`write ${segmentNum} ts of ${sbrName}`);
      // fs.writeFile(`${sbrName}_${segmentNum++}.ts`, Buffer.concat(buffers));
      putObject(
        outputBucket,
        `${sbrName}_${segmentNum++}.ts`,
        Buffer.concat(buffers),
      );

      if (isEnd(chunk)) {
        segmentNum = 0;
        const m3u8 = chunk
          .subarray(index)
          .toString()
          .replace(/.*\.ts$/gm, () => {
            return `${sbrName}_${segmentNum++}.ts`;
          });
        putObject(outputBucket, `${sbrName}.m3u8`, m3u8); // m3u8이 ts와 같이 들어오는 경우가 있으므로 subarray를 write해야됨
      }
      buffers.length = 0;
    } else {
      buffers.push(chunk);
    }
  });

  stream.on('end', function () {
    console.log(`${sbrName} Processing finished !`);
    resolve();
  });

  stream.on('error', function (err) {
    console.log(`${sbrName} Processing error occured !`);
    reject(err);
  });

  console.log(`${sbrName} Processing started !`);
  return stream;
}

function isEnd(chunk) {
  return (
    Buffer.compare(
      chunk.subarray(chunk.length - M3U8_END.length, chunk.length),
      M3U8_END,
    ) === 0
  );
}

function getStreamInfo(m3u8) {
  return m3u8.toString().split('\n')[2];
}
async function makeMasterManifest(outputBucket, fileName) {
  const streamInfoList = await Promise.all(
    resolutions.map((resolution) =>
      fs
        .readFile(`${resolution.name}.m3u8`)
        .then(
          (m3u8) =>
            `${getStreamInfo(m3u8)}\n${fileName}_${resolution.name}.m3u8`,
        ),
    ),
  );
  return putObject(
    outputBucket,
    `${fileName}_master.m3u8`,
    ['#EXTM3U', ...streamInfoList].join('\n'),
  );
}

async function putObject(bucketName, objectName, data) {
  const dataBody = Buffer.from(data);
  return S3.putObject({
    Bucket: bucketName,
    Key: objectName,
    Body: dataBody,
    ACL: 'public-read',
  })
    .promise()
    .then(() => console.log(`${bucketName} : ${objectName} upload done.`))
    .catch((e) => {
      console.error(`${bucketName} : ${objectName} upload failed.`);
      throw e;
    });
}

exports.main = main;
