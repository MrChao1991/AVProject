package com.cfox.videomuxersimple.splicevideo

import android.media.MediaCodec
import java.nio.ByteBuffer

data class SData (val buffer: ByteBuffer, val info: MediaCodec.BufferInfo) {}