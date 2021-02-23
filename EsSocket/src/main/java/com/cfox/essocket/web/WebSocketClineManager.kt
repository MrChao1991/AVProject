package com.cfox.essocket.web

import android.util.Log
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.nio.ByteBuffer

class WebSocketClineManager : SocketClineListener {
    companion object {
        private const val TAG = "WebSocketClineManager"
    }

    private var socketCline : SocketCline ? = null
    private var socketClineListener : SocketClineListener ? = null

    fun connectServer(host: String, port: Int, socketClineListener: SocketClineListener?) {
        this.socketClineListener = socketClineListener
        val url = URI("ws://$host:$port")
        socketCline = SocketCline(url,this)
        socketCline?.connect()
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        socketClineListener?.onOpen(handshakedata)
        val code = handshakedata?.httpStatus
        Log.d(TAG, "onOpen: code $code")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        socketClineListener?.onClose(code, reason, remote)
    }

    override fun onMessage(message: String?) {
        socketClineListener?.onMessage(message)
    }


    override fun onMessage(bytes: ByteBuffer?) {
        socketClineListener?.onMessage(bytes)
    }

    override fun onError(ex: Exception?) {
        socketClineListener?.onError(ex)
    }

    fun sendData(byteArray: ByteArray) {
        socketCline?.apply {
            if (!this.isClosed && !this.isClosing) {
                this.send(byteArray)
            }
        }
    }

    fun sendData(msg: String) {
        socketCline?.apply {
            if (this.isOpen && !this.isClosed && !this.isClosing) {
                Log.d(TAG, "sendData: $msg")
                this.send(msg)
            }
        }
    }
}