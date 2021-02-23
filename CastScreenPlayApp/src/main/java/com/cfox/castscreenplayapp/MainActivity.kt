package com.cfox.castscreenplayapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "PlayMainActivity"
    }

    private val playerManager  = PlayerManager(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<SurfaceView>(R.id.sf_play_view).holder.addCallback(object: SurfaceHolder.Callback{
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                Log.d(TAG, "surfaceChanged: =====>")
                val surface = holder.surface
                playerManager.startPlay(surface)

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
            }

        })
    }
}