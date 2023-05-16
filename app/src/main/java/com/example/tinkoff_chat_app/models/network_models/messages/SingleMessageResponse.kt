package com.example.tinkoff_chat_app.models.network_models.messages

import com.squareup.moshi.Json

data class SingleMessageResponse(
    @Json(name = "message")
    val message: Message
)