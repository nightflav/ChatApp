package com.example.tinkoff_chat_app.models.network_models.events_queue

import com.squareup.moshi.Json

data class RegisteredQueueResponse(
    @Json(name = "last_event_id")
    val lastEventId: Int,
    @Json(name = "queue_id")
    val queueId: String,
)