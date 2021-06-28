package com.cfox.x264simple

import android.graphics.SurfaceTexture
import android.util.Size
import android.view.Surface
import android.view.TextureView
import com.cfox.camera.log.EsLog
import com.cfox.camera.surface.SurfaceProvider

class SurfaceProviderImpl : SurfaceProvider {
    private val obj = Object()
    lateinit var textureView : AutoFitTextureView
    private var previewSize: Size? = null
    private var surface: Surface ? = null

    constructor(textureView: AutoFitTextureView) {
        this.textureView = textureView
        this.textureView.surfaceTextureListener = mTextureListener
    }


    override fun getSurface(): Surface {

        if (surface == null) {
            surface = Surface(textureView.surfaceTexture)
        }

        if (!surface!!.isValid) {
            EsLog.e("==>mSurface isValid false ")
        }
        return surface!!
    }

    override fun isAvailable(): Boolean {
        EsLog.d("subscribe: ..........")
        if (!textureView.isAvailable) {
            synchronized(obj) {
                if (!textureView.isAvailable) {
                    try {
                        obj.wait(10 * 1000.toLong())
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                if (!textureView.isAvailable) {
                    return false
                }
            }
        }
        EsLog.d("SurfaceTexture isAvailable width:" + textureView.width + "  height:" + textureView.height)
        return true
    }

    override fun getPreviewSurfaceClass(): Class<*> {
        return SurfaceTexture::class.java
    }

    override fun setAspectRatio(size: Size) {
        EsLog.d("setAspectRatio: size width:" + size.width + "  height:" + size.height)
        previewSize = size
        textureView.setAspectRatio(size.height, size.width)
        if (textureView.isActivated) {
            previewSize?.let {
                textureView.surfaceTexture!!.setDefaultBufferSize(it.width, it.height)
            }
        }
    }

    private val mTextureListener: TextureView.SurfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            EsLog.e("onSurfaceTextureAvailable: .......width:$width   height:$height     mPreviewSize:$previewSize  isValid:${textureView.isAvailable}")
            previewSize?.let {
                textureView.surfaceTexture!!.setDefaultBufferSize(it.width, it.height)
            }
            sendNotify()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            EsLog.d("onSurfaceTextureSizeChanged: ....")
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            EsLog.d("onSurfaceTextureDestroyed: ,,,,,,,")
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
//            EsLog.d("onSurfaceTextureUpdated: ,,,,,,,,");
        }
    }

    private fun sendNotify() {
        synchronized(obj) { obj.notifyAll() }
    }
}