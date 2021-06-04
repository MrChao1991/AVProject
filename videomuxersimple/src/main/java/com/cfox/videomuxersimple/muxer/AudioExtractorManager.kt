package com.cfox.videomuxersimple.muxer

class AudioExtractorManager (muxer: AVMuxer, audioMuxerInfos : MutableList<AudioMuxerInfo> ) {


    private val extractorList = mutableListOf<AudioExtractor>()

    init {
        audioMuxerInfos.forEach {
            extractorList.add(AudioExtractor(it))
        }
    }

}