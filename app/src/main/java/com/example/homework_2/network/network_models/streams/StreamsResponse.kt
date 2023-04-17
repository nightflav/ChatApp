package com.example.homework_2.network.network_models.streams

import com.squareup.moshi.Json

data class StreamsResponse(
    @Json(name = "streams")
    val streams: List<Stream>
)