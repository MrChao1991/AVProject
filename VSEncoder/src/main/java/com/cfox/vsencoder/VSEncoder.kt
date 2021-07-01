package com.cfox.vsencoder

class VSEncoder {
    private val ven = VSEncoderNative()

    fun initEncoder(format: VSFormat) {
        ven.initEncoder(
                format.width,
                format.height,
                format.fps,
                format.bitrate,
                format.i_frame_interval,
                format.b_frame,
                format.level_idc.level_Idc,
                format.profile_idc.profile_idc)
    }


    fun setListener(listener: VSEncoderListener) {
        ven.setListener(listener)
    }


    fun isAvailable() : Boolean {
        return ven.isAvailable()
    }

    fun start() {
        ven.start()
    }

    fun encodeYUVData(data : ByteArray) {
        ven.encodeYUVData(data)
    }

    fun release()  {
        ven.release()
    }

}