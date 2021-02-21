package com.cfox.essocket.web

import android.util.Log
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class SocketServer(port : Int, private val socketServerListener: SocketServerListener ) : WebSocketServer(InetSocketAddress(port)) {

    companion object {
        private const val TAG = "WebSocketServer"
    }

    private val socketMap = mutableMapOf<String, WebSocket>()

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        conn?.apply {
            val host = this.localSocketAddress.hostName
            val port = this.localSocketAddress.port
            val key = "$host:$port"
            Log.d(TAG, "onOpen: key: $key")
            socketMap[key] = this
        }

        socketServerListener.onOpen()
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        socketServerListener.onClose(code, reason, remote)
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        socketServerListener.onMessage(message)
    }

    override fun onMessage(conn: WebSocket?, message: ByteBuffer?) {
        socketServerListener.onMessage(message)
    }

    override fun onStart() {

    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        socketServerListener.onError(ex)
    }

    fun closeSocket(host: String, port: Int) {
        val key = "$host:$port"
        val socket = socketMap[key]
        socket?.let {
            it.close()
        }
        socketMap.remove(key)
    }

    fun getSocketKeySet(): Set<String> {
        return socketMap.keys
    }

    fun getSocket(key: String): WebSocket? {
        Log.d(TAG, "getSocket: key:$key")
        return socketMap[key]
    }
}
interface SocketServerListener {
    fun onOpen(){}

    fun onClose(code: Int, reason: String?, remote: Boolean){}

    fun onMessage(message: String?)

    fun onMessage(message: ByteBuffer?)

    fun onError(ex: Exception?){}
}