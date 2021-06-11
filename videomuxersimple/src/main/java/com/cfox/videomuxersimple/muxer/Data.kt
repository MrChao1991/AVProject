package com.cfox.videomuxersimple.muxer

import android.media.MediaCodec
import java.nio.ByteBuffer

interface Data {
    class EmptyData() : Data
    class EndData() : Data
    class VideoData(val buffer: ByteBuffer, val info : MediaCodec.BufferInfo, val startTime: Long) : Data
    class AudioData(val buffer: ByteArray, val info : MediaCodec.BufferInfo, val vol : Int) : Data
}