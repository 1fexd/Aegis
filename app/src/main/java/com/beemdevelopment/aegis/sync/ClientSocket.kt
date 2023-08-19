package com.beemdevelopment.aegis.sync

import android.util.Log
import com.beemdevelopment.aegis.otp.OtpInfoException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

@Serializable
sealed class WebSocketMessage {
    @Serializable
    @SerialName("RequestForDomain")
    data class RequestForDomain(val id: String, val domain: String) : WebSocketMessage()

    @Serializable
    @SerialName("CurrentToken")
    data class CurrentToken(
        val id: String,
        val twoFactorToken: String
    ) : WebSocketMessage()

    @Serializable
    @SerialName("DenySharing")
    data class DenySharing(val id: String) : WebSocketMessage()
}

interface RequestHandler {
    fun onRequestForDomain(id: String, domain: String)
}

class ClientSocket(
    private val handler: RequestHandler
) : WebSocketListener() {

    companion object {
        fun send(webSocket: WebSocket, message: WebSocketMessage) {
            webSocket.send(
                Json.encodeToString(
                    WebSocketMessage.serializer(),
                    message
                )
            )
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("Socket", text)
        val message = Json.decodeFromString(WebSocketMessage.serializer(), text)
        Log.d("Socket", "$message")

        if (message is WebSocketMessage.RequestForDomain) {
            handler.onRequestForDomain(message.id, message.domain)
        }
    }

    override fun onOpen(webSocket: okhttp3.WebSocket, response: okhttp3.Response) {
        Log.d("Socket", "Open")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        t.printStackTrace()
        Log.d("Socket", "Failed $t $response")
    }

    override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
        Log.d("Socket", "Closed")
    }
}