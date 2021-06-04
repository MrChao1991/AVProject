package com.cfox.videomuxersimple.muxer

import android.media.MediaExtractor
import android.media.MediaFormat


class AudioExtractor(audioMuxerInfo : AudioMuxerInfo ) {

    private val extractor : MediaExtractor
    private val trackIndex : Int
    private val format : MediaFormat
    init {
        extractor = MediaExtractor()
        extractor.setDataSource(audioMuxerInfo.audioPath)
        trackIndex = getTrackIndex()
        format = extractor.getTrackFormat(trackIndex)
        extractor.seekTo(audioMuxerInfo.startTime , MediaExtractor.SEEK_TO_CLOSEST_SYNC)
    }

    private fun getTrackIndex(): Int {
        val trackCount = extractor.trackCount
        for (i in 0 until  trackCount) {
            val format = extractor.getTrackFormat(i)
            val mine = format.getString(MediaFormat.KEY_MIME) ?: ""
            if (mine.startsWith("audio/")) {
                return i
            }
        }

        return  -1
    }

}

class AudioMuxerInfo(val audioPath: String, val startTime: Long, val endTime: Long) {

}