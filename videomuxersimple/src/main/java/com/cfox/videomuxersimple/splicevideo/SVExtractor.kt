package com.cfox.videomuxersimple.splicevideo

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import java.nio.ByteBuffer

class SVExtractor(videoPath: String) {

    companion object {
        private const val TAG = "SVExtractor"
    }

    private val extractor = MediaExtractor()
    private val videoTrackIndex : Int
    private var videoFormat : MediaFormat ? = null
    private val audioTrackIndex : Int
    private var audioFormat : MediaFormat ? = null
    private var videoDuration : Long = -1
    private var audioDuration : Long = -1
    private val videoInfo  = MediaCodec.BufferInfo()
    private val audioInfo  = MediaCodec.BufferInfo()
    private val videoBuffer : ByteBuffer
    private val audioBuffer : ByteBuffer
    init {
        extractor.setDataSource(videoPath)
        videoTrackIndex = getTrackIndex(true)
        audioTrackIndex = getTrackIndex(false)

        if (videoTrackIndex >= 0) {
            videoFormat = extractor.getTrackFormat(videoTrackIndex)
        }

        if (audioTrackIndex >= 0) {
            audioFormat = extractor.getTrackFormat(audioTrackIndex)
        }

        videoFormat?.let {
            videoDuration = it.getLong(MediaFormat.KEY_DURATION)
            Log.d(TAG, "videoDuration: $videoDuration")
        }

        audioFormat?.let {
            audioDuration = it.getLong(MediaFormat.KEY_DURATION)

            Log.d(TAG, "audioDuration:$audioDuration ")
        }

        val videoMaxBufferSize = videoFormat?.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)?: 12 * 1024
        videoBuffer = ByteBuffer.allocateDirect(videoMaxBufferSize)
        Log.d(TAG, "videoBufferSize: $videoMaxBufferSize ")

        val audioMaxBufferSize = audioFormat?.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)?: 12 * 1024
        audioBuffer = ByteBuffer.allocateDirect(audioMaxBufferSize)
        Log.d(TAG, "audioBufferSize:$audioMaxBufferSize ")

    }

    private fun getTrackIndex(isVideo : Boolean) : Int {
        val trackCount = extractor.trackCount
        for (i in 0 until trackCount) {
            val format = extractor.getTrackFormat(i)
            val mine = format.getString(MediaFormat.KEY_MIME) ?:""
            if (isVideo) {
                if (mine.startsWith("video/")) {
                    Log.d(TAG, "getTrackIndex: video trick index:$i")
                    return i
                }
            } else {
                if (mine.startsWith("audio/")) {
                    Log.d(TAG, "getTrackIndex: audio trick index:$i")
                    return i
                }
            }
        }

        return  -1
    }

    fun getVideoDuration () :Long {
        return videoDuration
    }

    fun getAudioDuration() : Long {
        return audioDuration
    }

    fun getVideoFormat() : MediaFormat ? {
        return videoFormat
    }

    fun getAudioFormat() : MediaFormat ? {
        return audioFormat
    }

    fun readSimpleVideoData(offsetPts: Long = 0L) : SData ?{
        extractor.selectTrack(videoTrackIndex)
        videoInfo.size = extractor.readSampleData(videoBuffer, 0)
        Log.d(TAG, "readSimpleVideoData: sampleTime:${extractor.sampleTime} ==>audioInfo.size:${audioInfo.size}  ===>offsetPts:$offsetPts   ===>dur:${offsetPts + extractor.sampleTime}")
        if (videoInfo.size > 0) {
            videoInfo.offset = 0
            videoInfo.presentationTimeUs = offsetPts + extractor.sampleTime
            videoInfo.flags = extractor.sampleFlags
            extractor.advance()
            return SData(videoBuffer, videoInfo)
        }
        Log.d(TAG, "readSimpleVideoData: video end ====> end")
        extractor.unselectTrack(videoTrackIndex)
//        extractor.selectTrack(audioTrackIndex)
//        extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
        return null
    }

    fun readSimpleAudioData(offsetPts: Long = 0L) : SData ?{
        extractor.selectTrack(audioTrackIndex)
        audioInfo.size = extractor.readSampleData(audioBuffer, 0)
        Log.d(TAG, "readSimpleAudioData: sampleTime: ${extractor.sampleTime}  ====> audioInfo.size:${audioInfo.size}   offsetPts:$offsetPts   dur:${offsetPts + extractor.sampleTime}")
        if (audioInfo.size > 0) {
            audioInfo.offset = 0
            audioInfo.presentationTimeUs = offsetPts + extractor.sampleTime
            audioInfo.flags = extractor.sampleFlags
            extractor.advance()
            return SData(audioBuffer, audioInfo)
        }
        Log.d(TAG, "readSimpleVideoData: audio end ====> end")
        extractor.unselectTrack(audioTrackIndex)
        return null
    }

    fun release(){
        extractor.release()
    }

}