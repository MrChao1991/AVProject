package com.cfox.videomuxersimple.muxer

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import java.nio.ByteBuffer

class AVMuxer(outPath: String)  {
    companion object {
        private const val TAG = "AVMuxer"
    }

    private var releaseIndex = 0
    val muxer : MediaMuxer = MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

    fun addTrack(format: MediaFormat): Int {
        return muxer.addTrack(format)
    }

    fun writeSimpleData(trackIndex : Int, byteBuf : ByteBuffer, bufferInfo : MediaCodec.BufferInfo) {
        muxer.writeSampleData(trackIndex, byteBuf, bufferInfo)
    }

    fun start() {
        Log.d(TAG, "start:---> ")
        muxer.start()
    }


    fun release() {
        releaseIndex++
        if (releaseIndex == 2) {
            Log.d(TAG, "release: muxer release --->")
            muxer.stop()
            muxer.release()
        }
    }
}