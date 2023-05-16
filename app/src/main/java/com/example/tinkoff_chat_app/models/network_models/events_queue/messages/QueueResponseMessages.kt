package com.example.tinkoff_chat_app.models.network_models.events_queue.messages

import com.squareup.moshi.Json

data class QueueResponseMessages(
    @Json(name = "events")
    val messageScreenEvents: List<MessageScreenEvent>,
)