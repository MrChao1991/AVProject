package com.cfox.videomuxersimple.splicevideo

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import com.cfox.videomuxersimple.muxer.AVMuxer
import java.nio.ByteBuffer

class SMuxer(outPut: String) {

    val muxer = MediaMuxer(outPut, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

    fun addTrack(format: MediaFormat) : Int {
        return muxer.addTrack(format)
    }

    fun writeSimpleData(trickIndex : Int , buffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        muxer.writeSampleData(trickIndex, buffer, info)
    }

    fun start() {
        muxer.start()
    }

    fun release() {
        muxer.stop()
        muxer.release()
    }
}