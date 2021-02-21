package com.cfox.essocket

import android.util.Log
import java.io.*
import java.net.Socket

class SocketManager(private val socket: Socket) {

    companion object {
        private const val TAG = "SocketManager"
        private const val BUFFER_SIZE = 2 * 1024 * 1024
    }

    private var starting = false
    var inputStream:InputStream ? = null
    var outputStream : OutputStream ? = null

    interface SocketMessageListener {
        fun onCallback(byte: ByteArray)
    }

    private var socketMessageListener : SocketMessageListener ? = null

    fun setListener(listener: SocketMessageListener) {
        this.socketMessageListener = listener
    }

    fun startReadSocket() {
        Log.d(TAG, "startReadSocket: .... end ...")
        starting = true
        try {
            inputStream = socket.getInputStream()

            inputStream?.let {
                val bufferTmp = ByteArray(BUFFER_SIZE)
                var bytes: Int
                while (starting) {
                    Log.d(TAG, "startReadSocket: .... read start ....")
                    bytes = it.read(bufferTmp)
                    Log.d(TAG, "startReadSocket: .... read end .... size:$bytes")

                    if (bytes > 0 && starting) {
                        val buffer = ByteArrayOutputStream(bytes)
                        buffer.write(bufferTmp, 0, bufferTmp.size)
                        socketMessageListener?.onCallback(buffer.toByteArray())
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "startReadSocket: ${e.printStackTrace()}")
        }
        Log.d(TAG, "startReadSocket: ...end...")
    }


    fun pushByte(byte : ByteArray) {
        Log.d(TAG, "pushByte: .... start....")

        try {
            if (socket.isConnected) {
                outputStream = socket.getOutputStream()
                outputStream?.write(byte)
                outputStream?.flush()
            }
        } catch (e: Exception) {
            Log.d(TAG, "pushByte: error : ${e.printStackTrace()}")
        }
        Log.d(TAG, "pushByte: ....end...")
    }

    fun close() {
        Log.d(TAG, "close: .... start...")
        starting = false
        try {
            inputStream?.close()
        }catch (e: Exception) {
            Log.d(TAG, "pushByte: close error ${e.printStackTrace()}")
        }

        try {
            outputStream?.flush()
            outputStream?.close()
        }catch (e: Exception) {
            Log.d(TAG, "pushByte: close error ${e.printStackTrace()} ")
        }

        try {
            if (!socket.isClosed) {
                socket.close()
            }
        } catch (e: Exception) {
            Log.d(TAG, "close: error :${e.printStackTrace()}")
        }
        Log.d(TAG, "close: ..... end ....")
    }

}