package com.cfox.yuvcamera

import android.util.Size
import com.cfox.camera.ConfigStrategy
import com.cfox.camera.log.EsLog
import java.util.*

class YuvConfigStrategy : ConfigStrategy {
    override fun getPreviewSize(size: Size, supportSizes: Array<Size>): Size {
        EsLog.d("getPreviewSize: size:" + size + "   supportSize:" + Arrays.toString(supportSizes))
        var resultSize: Size? = null
        var sizeTmp = size
        for (size1 in supportSizes) {
            if (size.width == size1.width) {
                if (size.height == size1.height) {
                    resultSize = size1
                    break
                } else {
                    if (Math.abs(size1.height - sizeTmp.height) < Math.abs(size.height - sizeTmp.height)) {
                        sizeTmp = size1
                    }
                }
            }
        }
        if (resultSize == null) {
            resultSize = sizeTmp
        }
        return resultSize
    }

    override fun getPictureSize(size: Size, supportSizes: Array<Size>): Size {
        EsLog.e("getPictureSize: size:" + size + "   supportSize:" + Arrays.toString(supportSizes))
        var resultSize: Size? = null
        var sizeTmp = size
        for (size1 in supportSizes) {
            if (size.width == size1.width) {
                if (size.height == size1.height) {
                    resultSize = size1
                    break
                } else {
                    if (Math.abs(size1.height - sizeTmp.height) < Math.abs(size.height - sizeTmp.height)) {
                        sizeTmp = size1
                    }
                }
            }
        }
        if (resultSize == null) {
            resultSize = sizeTmp
        }
        EsLog.d("getPictureSize: return picture size:$resultSize")
        return resultSize
    }

    override fun getPictureOrientation(cameraSensorOrientation: Int): Int {
        return cameraSensorOrientation
    }
}