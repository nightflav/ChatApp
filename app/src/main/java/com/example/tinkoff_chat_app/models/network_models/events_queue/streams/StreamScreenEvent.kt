package com.example.tinkoff_chat_app.models.network_models.events_queue.streams

import com.example.tinkoff_chat_app.models.network_models.streams.Stream
import com.example.tinkoff_chat_app.models.network_models.subscriptions.Subscription
import com.squareup.moshi.Json

data class StreamScreenEvent(
    @Json(name = "id")
    val id: Int,
    @Json(name = "type")
    val type: String,
    @Json(name = "subscriptions")
    val subscription: List<Subscription>?,
    @Json(name = "stream")
    val stream: List<Stream>?
)