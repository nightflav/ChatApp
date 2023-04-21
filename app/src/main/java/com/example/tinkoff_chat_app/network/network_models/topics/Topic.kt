package com.example.tinkoff_chat_app.network.network_models.topics

import com.squareup.moshi.Json

data class Topic(
    @Json(name = "name")
    val name: String
)