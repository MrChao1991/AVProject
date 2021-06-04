package com.cfox.videomuxersimple.muxer

class VideoExtractorManager(muxer: AVMuxer, videoMuxerInfo : VideoMuxerInfo ) {


    private val extractor : VideoExtractor

    init {
        extractor = VideoExtractor(videoMuxerInfo)

    }


}