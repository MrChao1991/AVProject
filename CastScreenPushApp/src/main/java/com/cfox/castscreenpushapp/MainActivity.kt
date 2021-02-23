package com.cfox.castscreenpushapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var screenRecorderManager : ScreenRecorderManager ? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
        setContentView(R.layout.activity_main)

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
        startActivityForResult(captureIntent, 1)

    }

    private fun checkPermission() : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 1)

        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Log.d(TAG, "onActivityResult:   11111")

            data?.let {
                val mediaProjection = mediaProjectionManager.getMediaProjection(resultCode,it)
                mediaProjection?.let {
                    screenRecorderManager = ScreenRecorderManager(this, it)

                }
            }

            Log.d(TAG, "onActivityResult:   33333")
        }
    }

    fun connectServer(view: View) {
        val host = findViewById<EditText>(R.id.et_host).text.toString()
        val port = findViewById<EditText>(R.id.et_host_port).text.toString().toInt()
        screenRecorderManager?.connectServer(host, port)

    }

    fun startRecorder(view: View) {
        screenRecorderManager?.startRecorder()

    }

    fun stopRecorder(view: View) {
        screenRecorderManager?.stopRecorder()

    }

    fun connectTest(view: View) {
        screenRecorderManager?.connectTest()
    }
}