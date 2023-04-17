package com.example.homework_2.network.network_models.streams

import com.squareup.moshi.Json

data class Stream(
    @Json(name = "name")
    val name: String = "empty",
    @Json(name = "stream_id")
    val streamId: Int = 0,
)