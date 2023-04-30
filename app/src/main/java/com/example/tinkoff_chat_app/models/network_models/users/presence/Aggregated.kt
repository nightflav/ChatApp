package com.example.tinkoff_chat_app.models.network_models.users.presence

import com.squareup.moshi.Json

data class Aggregated(
    @Json(name = "status")
    val status: String,
)