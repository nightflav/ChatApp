package com.example.tinkoff_chat_app.models.network_models.topics

import com.squareup.moshi.Json

data class Topic(
    @Json(name = "name")
    val name: String
)