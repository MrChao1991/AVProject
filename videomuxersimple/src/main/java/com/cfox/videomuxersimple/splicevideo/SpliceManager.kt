package com.cfox.videomuxersimple.splicevideo

import android.util.Log

class SpliceManager(val muxer: SMuxer, val baseExtractor : SVExtractor, val svExtractor: SVExtractor) : Runnable {

    companion object{
        private const val TAG = "SpliceManager"
    }

    private var videoTrackIndex = -1
    private var audioTrackIndex = -1

    init {
        baseExtractor.getVideoFormat()?.let {
            videoTrackIndex = muxer.addTrack(it)
        }

        baseExtractor.getAudioFormat()?.let {
            audioTrackIndex = muxer.addTrack(it)
        }
        Log.d(TAG, "trackIndex videoTrackIndex:$videoTrackIndex    audioTrackIndex:$audioTrackIndex")
    }

    fun start() {
        muxer.start()
        Thread(this).start()
    }

    override fun run() {
        Log.d(TAG, "run: start ===>")

        while (true) {
            val bufferData = baseExtractor.readSimpleVideoData()
            bufferData?.let {
                muxer.writeSimpleData(videoTrackIndex, it.buffer, it.info)
            }?:break
        }

        Log.d(TAG, "run: change audio ====>")
        while (true) {
            val bufferData = baseExtractor.readSimpleAudioData()
            bufferData?.let {
                muxer.writeSimpleData(audioTrackIndex, it.buffer, it.info)
            }?:break
        }


        // 这样拼接是有问题的，需要先解码，然后重新编码
        while (true) {
            val bufferData = svExtractor.readSimpleVideoData(baseExtractor.getVideoDuration())
            bufferData?.let {
                muxer.writeSimpleData(videoTrackIndex, it.buffer, it.info)
            }?:break
        }

        Log.d(TAG, "run: change audio ====>")
        while (true) {
            val bufferData = svExtractor.readSimpleAudioData(baseExtractor.getAudioDuration())
            bufferData?.let {
                muxer.writeSimpleData(audioTrackIndex, it.buffer, it.info)
            }?:break
        }

        baseExtractor.release()
        muxer.release()
        Log.d(TAG, "run: muxer end===>")
    }

}