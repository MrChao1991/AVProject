package com.cfox.essocket.web

import java.lang.Exception
import java.nio.ByteBuffer


class WebSocketServerManager : SocketServerListener {

    private var socketServer : SocketServer ? = null
    private var socketServerListener : SocketServerListener ? = null

    fun startServer(port: Int, socketServerListener : SocketServerListener) {
        this.socketServerListener = socketServerListener
        socketServer = SocketServer(port,this)
        socketServer?.start()
    }


    fun getSocketKeySet(): Set<String>  {
        return socketServer?.getSocketKeySet() ?: setOf()
    }

    fun sendData(key : String , byteArray: ByteArray) {
        socketServer?.apply {
            this.getSocket(key)?.apply {
                if (!this.isClosed && !this.isClosing) {
                    this.send(byteArray)
                }
            }
        }
    }

    fun sendData(key: String, msg: String) {
        socketServer?.apply {
            this.getSocket(key)?.apply {
                if (!this.isClosed && !this.isClosing) {
                    this.send(msg)
                }
            }
        }
    }

    override fun onOpen() {
        socketServerListener?.onOpen()
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        socketServerListener?.onClose(code, reason, remote)
    }

    override fun onMessage(message: String?) {
        socketServerListener?.onMessage(message)
    }

    override fun onMessage(message: ByteBuffer?) {
        socketServerListener?.onMessage(message)
    }

    override fun onError(ex: Exception?) {
        socketServerListener?.onError(ex)
    }
}