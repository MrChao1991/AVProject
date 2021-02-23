package com.cfox.screenrecorder

import android.hardware.display.DisplayManager
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.projection.MediaProjection

class ScreenRecorderManager(mediaProjection: MediaProjection) : Runnable {

    companion object {
        private const val WIDTH = 1080
        private const val HEIGHT = 1920
    }

    private val mediaCodec : MediaCodec
    private val encodeThread : Thread = Thread(this)
    private var recording = false

    init {
        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, WIDTH, HEIGHT)
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, WIDTH * HEIGHT)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC)
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        val surface = mediaCodec.createInputSurface()

        mediaProjection.createVirtualDisplay(
                "screen-recorder", WIDTH, HEIGHT, 1, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null)

    }


    fun startRecorder() {
        recording = true
        encodeThread.start()

    }

    fun stopRecorder() {
        recording = false
    }

    override fun run() {
        mediaCodec.start()
        val mediaCodecInfo = MediaCodec.BufferInfo()
        while (recording) {
            val outputBufferIndex = mediaCodec.dequeueOutputBuffer(mediaCodecInfo, 10000)
            if (outputBufferIndex >=0) {
                val byteBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)
                byteBuffer?.let {
                    val byteArray = ByteArray(mediaCodecInfo.size)
                    it.get(byteArray)
                    FrameUtils.writeBytes(byteArray)
                    FrameUtils.writeString(byteArray)
                }
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
            }
        }


    }

}