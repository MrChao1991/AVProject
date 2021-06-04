package com.cfox.videomuxersimple

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.VideoView
import com.cfox.espermission.EsPermissions
import com.jaygoo.widget.RangeSeekBar
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val videoView by lazy {
        findViewById<VideoView>(R.id.videoView)
    }

    private val rangeSeekBar by lazy {
        findViewById<RangeSeekBar>(R.id.rangeSeekBar)
    }

    private val musicVolume by lazy {
        findViewById<SeekBar>(R.id.musicSeekBar)
    }

    private val videoVolume by lazy {
        findViewById<SeekBar>(R.id.videoView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()

        copyFile()



    }

    private fun checkPermission() {
        val permissions = mutableListOf<String>()
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        EsPermissions(this).isGranted(permissions)




    }

    private fun startPlay(videoPath: String) {

        videoView.setVideoPath(videoPath)
        videoView.setOnPreparedListener {
            val videoParam = videoView.layoutParams
            videoParam.width = it.videoWidth
            videoParam.height = it.videoHeight
            videoView.layoutParams = videoParam

            val duration = it.duration / 1000
            rangeSeekBar.setRange(0f, duration.toFloat())
            rangeSeekBar.setValue(0f , duration.toFloat())
            rangeSeekBar.setOnRangeChangedListener { view, min, max, isFromUser ->
                videoView.seekTo((min * 1000).toInt())
            }
        }

        videoView.setOnCompletionListener {
            videoView.start()
        }
        videoView.start()

    }

    private fun copyFile() {
        Thread {
            val musicPath = File(Environment.getExternalStorageDirectory(), "music.mp3").absolutePath
            val videoPath = File(Environment.getExternalStorageDirectory(), "video.mp4").absolutePath

            try {
                copyAssets("music.mp3", musicPath)
                copyAssets("input.mp4", videoPath)

                Log.d(TAG, "copyFile: =====>  end")

                videoView.post {
                    startPlay(videoPath)
                }
            } catch (e: IOException) {
                e.printStackTrace()
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