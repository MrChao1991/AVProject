package com.cfox.castscreenpushapp

import android.content.Context
import android.hardware.display.DisplayManager
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.projection.MediaProjection
import android.util.Log
import com.cfox.essocket.web.WebSocketClineManager
import java.nio.ByteBuffer

class ScreenRecorderManager(context: Context, mediaProjection: MediaProjection) : Runnable {

    companion object {
        private const val TAG = "ScreenRecorderManager"
        private const val WIDTH = 1080
        private const val HEIGHT = 2160

        private const val H265_NAL_I = 19
        private const val H265_NAL_VPS = 32
    }

    private val webSocketClineManager = WebSocketClineManager()
    private val mediaCodec : MediaCodec
    private val encodeThread : Thread = Thread(this)
    private var recording = false

    init {
        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, WIDTH, HEIGHT)// h265
//        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, WIDTH, HEIGHT) // h264
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, WIDTH * HEIGHT) // 影响清晰度
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
//        mediaFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileHigh)
//        mediaFormat.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCProfileMain)
//        mediaFormat.setInteger("level", MediaCodecInfo.CodecProfileLevel.AVCProfileMain)


        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC)
//        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        val surface = mediaCodec.createInputSurface()

        mediaProjection.createVirtualDisplay(
                "-display", WIDTH, HEIGHT, 1, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null)

    }

    fun connectServer(host: String, port: Int) {
        webSocketClineManager.connectServer(host, port, null)
    }

    fun connectTest() {
        webSocketClineManager.sendData("连接成功")
    }

    fun startRecorder() {
        recording = true
        encodeThread.start()

    }

    fun stopRecorder() {
        recording = false
    }

    override fun run() {
        Log.d(TAG, "run: ......$recording")
        mediaCodec.start()
        val mediaCodecInfo = MediaCodec.BufferInfo()
        while (recording) {
            val outputBufferIndex = mediaCodec.dequeueOutputBuffer(mediaCodecInfo, 10000)
            if (outputBufferIndex >=0) {
                val byteBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)
                byteBuffer?.let {
//                    val byteArray = ByteArray(mediaCodecInfo.size)
//                    it.get(byteArray)
//                    FrameUtils.writeBytes(byteArray, "output_265.h265")
//                    FrameUtils.writeString(byteArray,"output_265.txt")
//                    FrameUtils.writeBytes(byteArray, "output_264.h264")
//                    FrameUtils.writeString(byteArray,"output_264.txt")

                    pushFrameH265(byteBuffer, mediaCodecInfo)
                }
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
            }
        }
    }

    private var vps_sps_pps_buf : ByteArray ? = null
    private fun pushFrameH265(byteBuffer: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {
        var offset = 4
        if (byteBuffer.get(2).toInt() == 0x01) {
            offset = 3
        }

        val type = byteBuffer.get(offset).toInt() and 0x7E shr 1
        when (type) {
            H265_NAL_VPS -> {
                vps_sps_pps_buf = ByteArray(bufferInfo.size)
                vps_sps_pps_buf?.let {
                    byteBuffer.get(it)
                }
            }
            H265_NAL_I -> {
                val bytes = ByteArray(bufferInfo.size)
                byteBuffer.get(bytes)

                vps_sps_pps_buf?.let {
                    val byteBuf = ByteArray(it.size + bufferInfo.size)
                    System.arraycopy(it, 0, byteBuf , 0, it.size)
                    System.arraycopy(bytes, 0, byteBuf, it.size, bytes.size)
                    webSocketClineManager.sendData(byteBuf)
                }
            }
            else -> {
                val bytes = ByteArray(bufferInfo.size)
                byteBuffer.get(bytes)
                webSocketClineManager.sendData(bytes)
            }
        }
    }

    private fun pushFrameH264(byteBuffer: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {

    }

}