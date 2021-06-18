package com.cfox.yuvcamera

import android.graphics.ImageFormat
import android.media.ImageReader
import android.util.Size
import com.cfox.camera.imagereader.ImageReaderProvider
import com.cfox.camera.log.EsLog

class PreviewImageReader(private val listenr : PreviewListener) : ImageReaderProvider(TYPE.PREVIEW) {

    private var y : ByteArray ? = null
    private var u : ByteArray ? = null
    private var v : ByteArray ? = null

    override fun createImageReader(previewSize: Size, captureSize: Size?): ImageReader {
        EsLog.d("createImageReader: previewSize width:" + previewSize.width + "  previewSize height:" + previewSize.height)
        return ImageReader.newInstance(previewSize.width, previewSize.height, ImageFormat.YUV_420_888, 2)
    }

    override fun onImageAvailable(reader: ImageReader) {
//        EsLog.d("onImageAvailable: preview frame ....")
        val image = reader.acquireNextImage() ?: return

        val planes = image.planes
        if (y == null) {
            //limit  是 缓冲区 所有的大小     position 起始大小
            y = ByteArray(planes[0].buffer.limit() - planes[0].buffer.position())
            u = ByteArray(planes[1].buffer.limit() - planes[1].buffer.position())
            v = ByteArray(planes[2].buffer.limit() - planes[2].buffer.position())
        }

        if (planes[0].buffer.remaining() == y!!.size) {
            planes[0].buffer.get(y)
            planes[1].buffer.get(u)
            planes[2].buffer.get(v)
        }
        listenr.onPreview(y!!, u!! , v!!, planes[0].rowStride)
        image.close()
    }

    interface PreviewListener {
        fun onPreview(y : ByteArray, u : ByteArray , v : ByteArray , stride : Int)
    }
}