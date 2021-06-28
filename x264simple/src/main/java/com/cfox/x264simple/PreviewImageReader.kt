package com.cfox.x264simple

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import android.media.ImageReader
import android.util.Size
import com.cfox.camera.imagereader.ImageReaderProvider
import com.cfox.camera.log.EsLog
import java.util.concurrent.locks.ReentrantLock

class PreviewImageReader(private val listenr : PreviewListener) : ImageReaderProvider(TYPE.PREVIEW) {

    private var y : ByteArray ? = null
    private var u : ByteArray ? = null
    private var v : ByteArray ? = null

    private val lock = ReentrantLock()

    override fun createImageReader(previewSize: Size, captureSize: Size?): ImageReader {
        EsLog.d("createImageReader: previewSize width:" + previewSize.width + "  previewSize height:" + previewSize.height)
        return ImageReader.newInstance(previewSize.width, previewSize.height, ImageFormat.YUV_420_888, 2)
    }

    override fun onImageAvailable(reader: ImageReader) {
//        EsLog.d("onImageAvailable: preview frame ....")
        val image = reader.acquireNextImage() ?: return

        lock.lock()
        listenr.onPreview(image)
        lock.unlock()
        image.close()
    }

    interface PreviewListener {
        fun onPreview(image: Image)
    }
}