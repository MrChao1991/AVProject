package com.cfox.essocket.web

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.nio.ByteBuffer

class SocketCline(uri: URI, private val socketClineListener: SocketClineListener) : WebSocketClient(uri) {

    override fun onOpen(handshakedata: ServerHandshake?) {
        socketClineListener.onOpen(handshakedata)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        socketClineListener.onClose(code, reason, remote)
    }

    override fun onMessage(message: String?) {
        socketClineListener.onMessage(message)
    }

    override fun onMessage(bytes: ByteBuffer?) {
        socketClineListener.onMessage(bytes)
    }

    override fun onError(ex: Exception?) {
        socketClineListener.onError(ex)
    }
}
interface SocketClineListener {

    fun onOpen(handshakedata: ServerHandshake?){}

    fun onClose(code: Int, reason: String?, remote: Boolean){}

    fun onMessage(message: String?)

    fun onMessage(bytes: ByteBuffer?)

    fun onError(ex: Exception?){}
}