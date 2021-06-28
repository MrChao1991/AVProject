package com.cfox.x264simple.x264

class x264Lib {
    companion object {
        init {
            System.loadLibrary("x264-lib")
        }
    }

    external fun native_init()

    external fun native_video_encode_info(width: Int, height: Int, fps: Int, bitrate: Int)

    external fun native_push_yuv_data(data : ByteArray)

    external fun native_release()

}