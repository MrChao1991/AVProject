package com.cfox.essocket

import android.util.Log
import java.net.Socket

class ClineSocketHelper {

    companion object {
        private const val TAG = "ClineSocketHelper"
    }

    private var socket : Socket ? = null

    fun connectSocket(host: String, port: Int) : Socket? {
        Log.d(TAG, "connectSocket:  ... start ....")
        try {
            socket = Socket(host, port)
        } catch (e: Exception) {
            Log.d(TAG, "connectSocket: connect error : ${e.printStackTrace()}")
        }
        Log.d(TAG, "connectSocket: .... end ....")
        return socket
    }
}