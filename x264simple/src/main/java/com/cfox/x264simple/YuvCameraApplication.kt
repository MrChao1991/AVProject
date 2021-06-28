package com.cfox.x264simple

import android.app.Application
import com.cfox.camera.EsCamera
import com.cfox.camera.log.EsLog

class YuvCameraApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        EsLog.setPrintTag("YUV-Camera")
        EsCamera.init(this)
    }
}