package com.cfox.videomuxersimple.muxer

import android.media.MediaExtractor
import android.media.MediaFormat

class VideoExtractor(videoMuxerInfo : VideoMuxerInfo ) {


    private val extractor : MediaExtractor
    private val trackIndex : Int
    private val format : MediaFormat
    init {
        extractor = MediaExtractor()
        extractor.setDataSource(videoMuxerInfo.videoPath)
        trackIndex = getVideoTrack()
        format = extractor.getTrackFormat(trackIndex)
        extractor.seekTo(videoMuxerInfo.startTime, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
    }



    private fun getVideoTrack(): Int {
        val trackCount = extractor.trackCount
        for (i in 0 until trackCount) {
            val format = extractor.getTrackFormat(i)
            val mine = format.getString(MediaFormat.KEY_MIME) ?: ""
            if (mine.startsWith("video/")) {
                return i
            }
        }
        return -1
    }






}

class VideoMuxerInfo(val videoPath: String, val startTime: Long, val endTime: Long) {

}