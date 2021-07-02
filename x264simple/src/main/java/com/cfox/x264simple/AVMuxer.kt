package com.cfox.x264simple

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import java.nio.ByteBuffer

class AVMuxer(path: String) {
    companion object {
        private const val TAG = "AVMuxer"
    }

    val muxer: MediaMuxer = MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

    fun addTrack(format: MediaFormat): Int {
        return muxer.addTrack(format)
    }

    fun writeSimpleData(trackIndex: Int, byteBuf: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {
        muxer.writeSampleData(trackIndex, byteBuf, bufferInfo)
    }

    fun start() {
        Log.d(TAG, "start:---> ")
        muxer.start()
    }


    fun release() {
        muxer.stop()
        muxer.release()
    }
}