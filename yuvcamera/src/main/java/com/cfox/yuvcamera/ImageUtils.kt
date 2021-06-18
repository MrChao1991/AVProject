package com.cfox.yuvcamera

import android.graphics.*
import java.io.ByteArrayOutputStream

object ImageUtils {

    fun yuvToBitmap(yuv : ByteArray, format: Int, width: Int , height: Int) : Bitmap {
        val yImage = YuvImage(yuv, format, width, height, null)
        val stream = ByteArrayOutputStream()
        yImage.compressToJpeg(Rect(0, 0, width, height), 100, stream)
        val bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0 , stream.size())
        return bitmap
    }
}