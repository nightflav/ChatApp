package com.example.tinkoff_chat_app.network.network_models.messages

import com.squareup.moshi.Json

data class MessageResponse(
    @Json(name = "messages")
    val messages: List<Message>,
)