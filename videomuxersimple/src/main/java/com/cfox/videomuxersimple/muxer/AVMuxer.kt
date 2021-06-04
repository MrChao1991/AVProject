package com.cfox.videomuxersimple.muxer

import android.media.MediaMuxer

class AVMuxer(outPath: String)  {

    val muxer : MediaMuxer

    init {
        muxer = MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    }

}