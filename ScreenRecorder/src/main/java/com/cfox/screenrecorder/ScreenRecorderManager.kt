package com.cfox.screenrecorder

import android.hardware.display.DisplayManager
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.projection.MediaProjection

class ScreenRecorderManager(private val mediaProjection: MediaProjection) : Runnable {

    companion object {
        private const val WIDTH = 1080
        private const val HEIGHT = 2160
    }

    private val mediaCodec : MediaCodec
    private val encodeThread : Thread = Thread(this)
    private var recording = false

    init {
//        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, WIDTH, HEIGHT)// h265
        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, WIDTH, HEIGHT) // h264
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, WIDTH * HEIGHT) // 影响清晰度
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
//        mediaFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileHigh)
//        mediaFormat.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCProfileMain)
//        mediaFormat.setInteger("level", MediaCodecInfo.CodecProfileLevel.AVCProfileMain)


//        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC)
        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

    }


    fun startRecorder() {
        recording = true
        encodeThread.start()

    }

    fun stopRecorder() {
        recording = false
    }

    override fun run() {
        val surface = mediaCodec.createInputSurface()

        mediaProjection.createVirtualDisplay(
                "-display", WIDTH, HEIGHT, 1, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null)

        mediaCodec.start()
        val mediaCodecInfo = MediaCodec.BufferInfo()
        while (recording) {
            val outputBufferIndex = mediaCodec.dequeueOutputBuffer(mediaCodecInfo, 10000)
            if (outputBufferIndex >=0) {
                val byteBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)
                byteBuffer?.let {
                    val byteArray = ByteArray(mediaCodecInfo.size)
                    it.get(byteArray)
//                    FrameUtils.writeBytes(byteArray, "output_265.h265")
//                    FrameUtils.writeString(byteArray,"output_265.txt")
                    FrameUtils.writeBytes(byteArray, "output_264.h264")
                    FrameUtils.writeString(byteArray,"output_264.txt")
                }
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
            }
        }


    }

}