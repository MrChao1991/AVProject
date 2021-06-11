package com.cfox.videomuxersimple.muxer

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import java.nio.ByteBuffer

class VideoExtractor(val manager: VideoExtractorManager, val videoMuxerInfo : VideoMuxerInfo ) {

    companion object {
        private const val TAG = "VideoExtractor"
    }

    private val extractor : MediaExtractor = MediaExtractor()
    private val trackIndex : Int
    private val format : MediaFormat
    private val maxBufferSize : Int
    private val buffer : ByteBuffer
    private val info : MediaCodec.BufferInfo
    init {
        extractor.setDataSource(videoMuxerInfo.videoPath)
        trackIndex = getVideoTrack()
        extractor.selectTrack(trackIndex)
        format = extractor.getTrackFormat(trackIndex)
        extractor.seekTo(videoMuxerInfo.startTime, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
        maxBufferSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
        buffer = ByteBuffer.allocateDirect(maxBufferSize)
        info = MediaCodec.BufferInfo()
    }

    fun getFormat() : MediaFormat {
        return format
    }

    fun readSimpleMediaData() : Data {
        Log.d(TAG, "readSimpleMediaData: .....")
        val simpleTimeUs = extractor.sampleTime
        if (simpleTimeUs == -1L) {
            return Data.EndData()
        }

        if (simpleTimeUs < videoMuxerInfo.startTime
                || simpleTimeUs > videoMuxerInfo.endTime) {
            extractor.advance()
            return Data.EmptyData()
        }

        info.presentationTimeUs = simpleTimeUs - videoMuxerInfo.startTime + 600
        info.flags = extractor.sampleFlags
        info.size = extractor.readSampleData(buffer, 0)
        if (info.size < 0) {
            return Data.EmptyData()
        }

        Log.d(TAG, "readSimpleMediaData: write ....")
//        manager.writeSimpleData(buffer, info)
        extractor.advance()

        return Data.VideoData(buffer, info, videoMuxerInfo.startTime)
    }

    fun release() {
        extractor.release()
    }

    private fun getVideoTrack(): Int {
        val trackCount = extractor.trackCount
        for (i in 0 until trackCount) {
            val format = extractor.getTrackFormat(i)
            val mine = format.getString(MediaFormat.KEY_MIME) ?: ""
            if (mine.startsWith("video/")) {
                return i
            }
        }
        return -1
    }






}

class VideoMuxerInfo(val videoPath: String, val startTime: Long, val endTime: Long) {}

