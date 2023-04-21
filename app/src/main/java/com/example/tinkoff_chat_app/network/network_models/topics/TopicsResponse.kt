package com.example.tinkoff_chat_app.network.network_models.topics

import com.squareup.moshi.Json

data class TopicsResponse(
    @Json(name = "topics")
    val topics: List<Topic>
)