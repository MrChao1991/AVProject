package com.cfox.essocket

import android.util.Log
import java.io.IOException
import java.net.ServerSocket

class ServerSocketHelper {
    companion object {
        private const val TAG = "ServerSocketHelper"
    }

    private val socketMap = mutableMapOf<String, SocketManager>()
    private var serverSocket : ServerSocket ? = null
    private var serverStarting = false

    fun startServer(port: Int) {
        Log.d(TAG, "startServer: ..... start .... port:$port")
        try {
            serverStarting = true
            serverSocket = ServerSocket(port)
            while (serverStarting) {
                Log.d(TAG, "startServer:serverSocket is null =====>  ${serverSocket == null}")
                Log.d(TAG, "startServer: inetAddress : ${serverSocket?.localSocketAddress}")
                val socket = serverSocket?.accept()
                socket?.let {
                    val host = it.inetAddress.hostAddress
                    val port = it.port
                    val key = "$host:$port"
                    Log.d(TAG, "startServer: cline : host:$host  port:$port   key:$key")
                    socketMap[key] = SocketManager(socket)
                }
            }
        } catch (e: IOException) {
            Log.d(TAG, "startServer: fail : ${e.printStackTrace()}")
        }
        Log.d(TAG, "startServer: ..... end ....")
    }

    fun closeServer() {
        Log.d(TAG, "closeServer:  start ....")
        serverStarting = false
        socketMap.forEach{
            it.value.close()
        }

        serverSocket?.let {
            if (!it.isClosed) {
                it.close()
            }
        }
        Log.d(TAG, "closeServer: ... end ....")
    }

    fun closeSocket(host: String, port: Int) {
        val key = "$host:$port"
        val socket = socketMap[key]
        socket?.let {
            it.close()
        }
        socketMap.remove(key)
    }

    fun getSocketKeySet() : Set<String> {
        return socketMap.keys
    }

    fun getSocket(key : String) : SocketManager? {
        Log.d(TAG, "getSocket: key:$key")
        return socketMap[key]
    }
}