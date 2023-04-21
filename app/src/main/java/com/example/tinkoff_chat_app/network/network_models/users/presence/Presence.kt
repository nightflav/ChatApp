package com.example.tinkoff_chat_app.network.network_models.users.presence

import com.squareup.moshi.Json

data class Presence(
    @Json(name = "aggregated")
    val aggregated: Aggregated
)