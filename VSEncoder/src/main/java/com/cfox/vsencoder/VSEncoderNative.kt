package com.cfox.vsencoder

import android.os.Handler
import android.os.HandlerThread

class VSEncoderNative {
    companion object {
        init {
            System.loadLibrary("x264-lib")
        }
    }

    private external fun native_init()
    private external fun native_init_encode_params(width: Int, height: Int, fps: Int, bitrate: Int, iFrameInterval: Int, bFrame: Int, levelIdc: Int, profileIdc: String)
    private external fun native_start()
    private external fun native_encode_yuv_data(data : ByteArray)
    private external fun native_release()

    private var encoderThread : HandlerThread ? = null
    private var encoderHandler : Handler ? = null

    private var listener : VSEncoderListener ? = null
    private var isAvailable = false

    fun setListener(listener: VSEncoderListener) {
        this.listener = listener
    }

    private fun nativeEncodeData(byteArray: ByteArray) {
        listener?.onEncodeFrame(byteArray)
    }

    private fun nativeEncodeStatus(code : Int) {
        if (code == 1) {
            // init success
        } else if (code == 2) {
            isAvailable = true
        }
    }

    fun isAvailable() : Boolean {
        return isAvailable
    }

    fun initEncoder(width: Int, height: Int, fps: Int, bitrate: Int, iFrameInterval: Int, bFrame: Int, levelIdc: Int, profileIdc: String) {
        startHandlerIfNeed()
        isAvailable = false
        postRunnable {
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

        }
    }


    fun start() {
        postRunnable {
            listener?.onStartEncoder()
            native_start()
        }
    }

    fun encodeYUVData(data : ByteArray) {
        postRunnable {
            native_encode_yuv_data(data)
        }
    }

    fun release()  {
        postRunnable {
            native_release()
        }
        encoderThread?.quitSafely()
        encoderThread = null
        encoderHandler = null
    }


    private fun postRunnable(run : ()->Unit) {
        encoderHandler?.let {
            it.post(run)
        }
    }

    private fun startHandlerIfNeed() {
        if (encoderThread == null) {
            encoderThread = HandlerThread("vs-encoder-t")
            encoderThread?.start()
        }

        encoderThread?.let {
            if (encoderHandler == null) {
                encoderHandler = Handler(it.looper)
            }
        }
    }

}