package com.cfox.x264simple

import android.Manifest
import android.graphics.*
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.cfox.camera.EsCamera
import com.cfox.camera.EsCameraManager
import com.cfox.camera.capture.PhotoCapture
import com.cfox.camera.request.FlashState
import com.cfox.camera.request.PreviewRequest
import com.cfox.espermission.EsPermissions
import com.cfox.x264simple.x264.x264Lib
import java.util.*

class MainActivity : AppCompatActivity(), PreviewImageReader.PreviewListener {


    private var esCameraManager : EsCameraManager ? = null
    private var photoCapture : PhotoCapture ? = null

    private var x264 :x264Lib ? = null

    private val previewTextureView by lazy {
        findViewById<AutoFitTextureView>(R.id.preview_texture_view)
    }

    private val yView  by lazy {
        findViewById<ImageView>(R.id.preview_y)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!checkPermissions()) {
            finish()
        }

        esCameraManager = EsCamera.createCameraManager(this, YuvConfigStrategy())
        esCameraManager?.let {
            val capture = it.photoModule()
            if (capture is PhotoCapture) {
                photoCapture = capture
            }
        }

        startPreview()
    }


    private fun checkPermissions() : Boolean {
        val permissions: MutableList<String> = ArrayList()
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.CAMERA)
        var permissionResult = EsPermissions(this).isGranted(permissions)
        if (!permissionResult) {
            EsPermissions(this).request(permissions, {
                permissionResult = true
            }, { _, _, _ ->
                permissionResult = false
            })
        }

        return permissionResult
    }

    private fun startPreview() {
        val builder = getRequest()
        builder.openBackCamera()
        builder.setSurfaceProvider(SurfaceProviderImpl(previewTextureView))
        photoCapture?.let {
            it.onStartPreview(builder.builder()) { }
        }
    }

    private fun getRequest() : PreviewRequest.Builder{
        val previewSize = Size(1080, 1440)
        return PreviewRequest.createBuilder()
                .setPreviewSize(previewSize)
                .setPictureSize(previewSize, ImageFormat.JPEG)
                .setFlash(FlashState.OFF)
                .addImageReaderProvider(PreviewImageReader(this))
//                .addImageReaderProvider(new CaptureImageReader())
    }

    override fun onPreview(image: Image) {
        val width = image.width
        val height = image.height
        val data = ImageUtil.getBytesFromImageAsType(image, 2)
        val nv21 = ByteArray(data.size)
        YUVTools.rotateSP(data, nv21, width, height, 90)

        if (x264 == null) {
            x264 = x264Lib()
            x264?.native_init()
            x264?.native_video_encode_info(1080, 1440, 30, 1440* 1080 * 5)
        }

        x264?.native_push_yuv_data(nv21)


        val bitmap = ImageUtils.yuvToBitmap(nv21, ImageFormat.NV21, height, width)
        yView.post {
            bitmap.let {
                yView.setImageBitmap(it)
            }
        }
    }
}