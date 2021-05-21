package com.cfox.audiomix

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cfox.espermission.EsPermissions
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissions: MutableList<String> = ArrayList()
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        EsPermissions(this).isGranted(permissions)
    }

    fun startMix(view: View) {

        Thread {
            val musicPath = File(Environment.getExternalStorageDirectory(), "music.mp3").absolutePath
            val videoPath = File(Environment.getExternalStorageDirectory(), "video.mp4").absolutePath

            try {
                copyAssets("music.mp3", musicPath)
                copyAssets("input2.mp4", videoPath)

            } catch (e: IOException) {
                e.printStackTrace()
            }

            val mixMusicPath = File(Environment.getExternalStorageDirectory(), "music.mp3").absolutePath
            val mixVideoPath = File(Environment.getExternalStorageDirectory(), "video.mp4").absolutePath

            try {

                val mixOutMusicPath = File(Environment.getExternalStorageDirectory(), "mixMusic.mp3").absolutePath
                val mixOutVideoPath = File(Environment.getExternalStorageDirectory(), "mixVideo.mp3").absolutePath
                AudioClipTools().clip(mixMusicPath, mixOutMusicPath, 60 * 1000 * 1000, 70 * 1000 * 1000)
                AudioClipTools().clip(mixVideoPath, mixOutVideoPath, 60 * 1000 * 1000, 70 * 1000 * 1000)

                Log.d(TAG, "startMix: clip end ===>")
            } catch (e: Exception) {

            }

        }.start()


    }


    private fun copyAssets(assetsName: String, path: String) {
        val assetFileDescriptor = assets.openFd(assetsName)
        val from = FileInputStream(assetFileDescriptor.fileDescriptor).channel
        val to = FileOutputStream(path).channel
        from.transferTo(assetFileDescriptor.startOffset, assetFileDescriptor.length, to)

    }

}