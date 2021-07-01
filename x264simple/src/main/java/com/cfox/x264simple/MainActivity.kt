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
import com.cfox.vsencoder.*
import java.util.*

class MainActivity : AppCompatActivity(), PreviewImageReader.PreviewListener {


    private var esCameraManager : EsCameraManager ? = null
    private var photoCapture : PhotoCapture ? = null

    private lateinit var vsEncoder  : VSEncoder


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

        val format = VSFormat(1080, 1440)
        format.b_frame = 5
        format.fps = 30
        format.bitrate = (format.width * format.height * 2.5).toInt()
        format.i_frame_interval = 2
        format.profile_idc = PROFILE_IDC.HIGH
        format.level_idc = LEVEL_IDC.HD_720P_31
        vsEncoder = VSEncoder()
        vsEncoder.initEncoder(format)
        vsEncoder.setListener(object: VSEncoderListener {

            override fun onStartEncoder() {

            }

            override fun onFail(code: Int) {

            }

            override fun onEncodeFrame(byteArray: ByteArray) {
                FileUtils.writeBytes(byteArray)
                FileUtils.writeContent(byteArray)
            }

        })

        vsEncoder.start()

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

        if (vsEncoder.isAvailable()) {
            vsEncoder.encodeYUVData(nv21)
        }


        val bitmap = ImageUtils.yuvToBitmap(nv21, ImageFormat.NV21, height, width)
        yView.post {
            bitmap.let {
                yView.setImageBitmap(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vsEncoder.release()
    }
}