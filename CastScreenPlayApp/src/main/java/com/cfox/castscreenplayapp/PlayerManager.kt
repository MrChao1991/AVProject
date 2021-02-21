package com.cfox.castscreenplayapp

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import com.cfox.essocket.web.SocketServerListener
import com.cfox.essocket.web.WebSocketServerManager
import java.nio.ByteBuffer

class PlayerManager : SocketServerListener{

    companion object {
        private const val TAG = "PlayerManager"

        private const val PORT = 7801
    }

    private var mediaCodec : MediaCodec ? = null

    private val webSocketServerManager = WebSocketServerManager()


    fun startPlay(surface : Surface) {
        startSocketServer(PORT)
        initDecoder(surface)
    }

    private fun startSocketServer(port: Int) {
        webSocketServerManager.startServer(port, this)
    }

    private fun initDecoder(surface: Surface) {

        // 创建H265 解码的MediaCodec
        mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC)
        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, 1080, 1920)
        format.setInteger(MediaFormat.KEY_BIT_RATE, 1080 * 1920)// 设置比特率
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 20) // 设置帧率
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1) // 设置多长时间一个关键帧 （I 帧）， 在解码的时候设置无效

        mediaCodec?.configure(format, surface, null, 0)
        mediaCodec?.start()
    }

    override fun onMessage(message: String?) {

    }

    override fun onMessage(message: ByteBuffer?) {
        Log.d(TAG, "onMessage: size:${message?.remaining()}")

        message?.apply {
            val buffer = ByteArray(this.remaining())
            this.get(buffer)
            decodeFrameDate(buffer)
        }
    }

    private fun decodeFrameDate(bytes : ByteArray) {

        mediaCodec?.let {
            // 输入解码数据
            val inIndex = it.dequeueInputBuffer(100000)
            if (inIndex >= 0) {
                val buffer = it.getInputBuffer(inIndex)
                buffer?.clear()
                buffer?.put(bytes, 0, bytes.size)
                // 解码
                it.queueInputBuffer(inIndex, 0, bytes.size, System.currentTimeMillis(), 0)
            }

            // 取出解码数据
            val bufferInfo = MediaCodec.BufferInfo()
            var outIndex = it.dequeueOutputBuffer(bufferInfo, 100000)
            while (outIndex > 0) {
                it.releaseOutputBuffer(outIndex, true)
                outIndex = it.dequeueOutputBuffer(bufferInfo, 0)
            }
        }
    }
}