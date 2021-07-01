package com.cfox.vsencoder

interface VSEncoderListener {

    fun onStartEncoder()

    fun onFail(code: Int)

    fun onEncodeFrame(byteArray: ByteArray)

}