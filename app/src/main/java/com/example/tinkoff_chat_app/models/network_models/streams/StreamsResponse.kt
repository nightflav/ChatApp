package com.example.tinkoff_chat_app.models.network_models.streams

import com.squareup.moshi.Json

data class StreamsResponse(
    @Json(name = "streams")
    val streams: List<Stream>
)