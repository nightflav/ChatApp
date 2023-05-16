package com.example.tinkoff_chat_app.models.network_models.events_queue.streams

import com.squareup.moshi.Json

data class QueueResponseStreams(
    @Json(name = "events")
    val messageScreenEvents: List<StreamScreenEvent>,
)