package org.taxidriver.websocketclient

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import org.taxidriver.app.App.Companion.channel
import org.taxidriver.utils.*
import org.taxidriver.websocketclient.listeners.NewMessageListener
import java.net.URI
import java.net.URISyntaxException

class TaxiWebSocketClient {

    private var web_socket_client: WebSocketClient? = null
    internal var newMessageListener: NewMessageListener? = null

    internal constructor() {

    }

    internal fun connectToServer() {
        var uri: URI? = null
        try {
            val server_address = WEBSOCKET_SERVER_ADDRESS
            val server_port = WEBSOCKET_SERVER_PORT
            uri = URI("ws://$server_address:$server_port")

        } catch (e: URISyntaxException) {

        }
        web_socket_client = object : WebSocketClient(uri, Draft_6455(), HashMap<String, String>(),0) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                val jsonObject = JSONObject()
                jsonObject.put("state", STATE_OPENED)
                /*try{
                    channel.put(jsonObject.toString())
                } catch (e: InterruptedException) {

                }*/
            }

            override fun onMessage(message: String?) {
                Log.d("onMessage", message)
                val jsonObject = JSONObject(message)
                jsonObject.put("state", STATE_NEW_MESSAGE)
                try{
                    channel.put(jsonObject)
                    newMessageListener?.onNewMessage()
                } catch (e: InterruptedException) {
                    Log.d("onMessage", e.message)
                }
            }

            override fun onError(ex: Exception?) {
                val jsonObject = JSONObject()
                jsonObject.put("state", STATE_ERROR)
                jsonObject.put("error", ex?.message)
                try{
                    channel.put(jsonObject)
                } catch (e: InterruptedException) {

                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                val jsonObject = JSONObject()
                jsonObject.put("state", STATE_CLOSED)
                try{
                    channel.put(jsonObject)
                } catch (e: InterruptedException) {

                }
            }

        }
        web_socket_client?.connect()
    }

    internal fun sendMessage(msg: String): Boolean {
        val connectionOpen = web_socket_client?.isOpen()?:false
        if (connectionOpen) {
            web_socket_client?.send(msg)
            return true
        } else{
            return false
        }
    }

    internal fun isOpen() = web_socket_client?.isOpen()?:false

    internal fun close() = web_socket_client?.close()
}