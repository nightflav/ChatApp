package com.example.homework_2.network.networkModels.messages

import com.squareup.moshi.Json

data class MessageResponse(
    @Json(name = "messages")
    val messages: List<Message>,
)