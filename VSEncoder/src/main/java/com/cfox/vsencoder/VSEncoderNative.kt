package com.cfox.vsencoder

class VSEncoderNative {
    companion object {
        init {
            System.loadLibrary("x264-lib")
        }
    }

    private external fun native_init()
    private external fun native_init_encode_params(width: Int, height: Int, fps: Int, bitrate: Int, iFrameInterval: Int, bFrame: Int, levelIdc: Int, profileIdc: String)
    private external fun native_start()
    private external fun native_encode_yuv_data(data: ByteArray)
    private external fun native_release()

    private var listener: VSEncoderListener? = null
    private var isAvailable = false
    private var isEncodeAvailable = false

    fun setListener(listener: VSEncoderListener) {
        this.listener = listener
    }

    private fun nativeEncodeData(typte: Int, byteArray: ByteArray) {
        listener?.onEncodeFrame(byteArray)
    }

    private fun nativeEncodeStatus(code: Int) {
        if (code == 1) {
            // init success
        } else if (code == 2) {
            isEncodeAvailable = true
        }
    }

    fun isAvailable(): Boolean {
        return isAvailable
    }

    fun initEncoder(width: Int, height: Int, fps: Int, bitrate: Int, iFrameInterval: Int, bFrame: Int, levelIdc: Int, profileIdc: String) {
        isAvailable = false
        isEncodeAvailable = false
        native_init()
        native_init_encode_params(
                width,
                height,
                fps,
                bitrate,
                iFrameInterval,
                bFrame,
                levelIdc,
                profileIdc)

        native_start()

    }


    fun start() {
        listener?.onStartEncoder()
        if (isEncodeAvailable) {
            isAvailable = true
        }
    }

    fun stop() {
        isAvailable = false
    }

    fun encodeYUVData(data: ByteArray) {
        if (isAvailable) {
            native_encode_yuv_data(data)
        }
    }

    fun release() {
        native_release()
    }

}