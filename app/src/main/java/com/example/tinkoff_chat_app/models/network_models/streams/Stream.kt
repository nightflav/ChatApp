package com.example.tinkoff_chat_app.models.network_models.streams

import com.squareup.moshi.Json

data class Stream(
    @Json(name = "name")
    val name: String = "empty",
    @Json(name = "stream_id")
    val streamId: Int = 0,
    @Json(ignore = true)
    val isSubscriptionStream: Boolean = false
)