package com.example.tinkoff_chat_app.models.network_models.users.presence.allUsersPresence

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Website(
    @Json(name = "status")
    val status: String,
)