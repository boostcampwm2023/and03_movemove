package com.everyone.movemove_android.ui.main.uploading_video

import android.annotation.SuppressLint
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import java.nio.ByteBuffer

class VideoTrimmer(
    private val originalPath: String,
    newPath: String,
    private val startMs: Long,
    private val endMs: Long
) {
    private val mediaExtractor = MediaExtractor()
    private val mediaMuxer = MediaMuxer("$newPath.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    private val mediaMetadataRetriever = MediaMetadataRetriever()
    private val indexMap = HashMap<Int, Int>(TRACK_COUNT)
    private var bufferSize = -1

    init {
        initMediaExtractor()
        initMediaMetadataRetriever()
        initMuxer()
    }

    @SuppressLint("WrongConstant")
    fun trim() {
        val outputBuffer = ByteBuffer.allocate(bufferSize)
        val bufferInfo = MediaCodec.BufferInfo()

        if (bufferSize < 0) {
            bufferSize = DEFAULT_BUFFER_SIZE
        }

        if (startMs > 0) {
            mediaExtractor.seekTo((startMs * 1000), MediaExtractor.SEEK_TO_CLOSEST_SYNC)
        }

        try {
            mediaMuxer.start()

            while (true) {
                bufferInfo.offset = 0
                bufferInfo.size = mediaExtractor.readSampleData(outputBuffer, 0)

                if (bufferInfo.size == -1) {
                    break
                } else {
                    bufferInfo.presentationTimeUs = mediaExtractor.sampleTime
                    if (endMs > 0 && bufferInfo.presentationTimeUs > endMs * 1000) {
                        break
                    } else {
                        bufferInfo.flags = mediaExtractor.sampleFlags
                        indexMap[mediaExtractor.sampleTrackIndex]?.let { trackIndex ->
                            mediaMuxer.writeSampleData(
                                trackIndex,
                                outputBuffer,
                                bufferInfo
                            )

                            mediaExtractor.advance()
                        }
                    }
                }
            }

            mediaMuxer.stop()
        } catch (e: IllegalStateException) {
            releaseComponents()
        }
    }

    private fun initMediaExtractor() {
        mediaExtractor.setDataSource(originalPath)
    }

    private fun initMediaMetadataRetriever() {
        mediaMetadataRetriever.setDataSource(originalPath)
    }

    private fun initMuxer() {
        addTracksToMuxer()
        setOutputVideoOrientation()
    }

    private fun addTracksToMuxer() {
        for (i in 0 until TRACK_COUNT) {
            val format = mediaExtractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)

            mime?.let {
                if (mime.startsWith("audio/") || mime.startsWith("video/")) {
                    mediaExtractor.selectTrack(i)
                    val dstIndex = mediaMuxer.addTrack(format)
                    indexMap[i] = dstIndex
                    if (format.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
                        val newSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
                        bufferSize = if (newSize > bufferSize) newSize else bufferSize
                    }
                }
            } ?: run {
                releaseComponents()
            }
        }
    }

    private fun setOutputVideoOrientation() {
        val degreeInString = mediaMetadataRetriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION
        )

        if (degreeInString != null) {
            val degrees = degreeInString.toInt()
            if (degrees >= 0) {
                mediaMuxer.setOrientationHint(degrees)
            }
        }
    }

    private fun releaseComponents() {
        try {
            mediaExtractor.release()
            mediaMuxer.release()
            mediaMetadataRetriever.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TRACK_COUNT = 2
    }
}