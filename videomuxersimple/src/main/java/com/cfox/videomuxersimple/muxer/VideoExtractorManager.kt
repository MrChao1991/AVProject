package com.cfox.videomuxersimple.muxer

import android.media.MediaCodec
import android.util.Log
import java.nio.ByteBuffer

class VideoExtractorManager(val muxer: AVMuxer, videoMuxerInfo : VideoMuxerInfo ) : Runnable {

    companion object {
        private const val TAG = "VideoExtractorManager"
    }

    private val extractor : VideoExtractor = VideoExtractor(this,videoMuxerInfo)

    private val muxerIndex : Int

    init {
        muxerIndex = muxer.addTrack(extractor.getFormat())
    }

    fun start() {
        Log.d(TAG, "start:  ---->>>")
        Thread(this).start()
    }

    override fun run() {

        while (true) {
            val data = extractor.readSimpleMediaData()
            if (data is Data.MediaData) {
                muxer.writeSimpleData(muxerIndex, data.buffer, data.info)
            } else if (data is Data.EndData) {
                extractor.release()
                muxer.release()
                break
            }
        }

        Log.d(TAG, "run: video manager run  end   ....")
    }
}