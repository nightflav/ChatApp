package com.example.tinkoff_chat_app.models.network_models.users.presence

import com.squareup.moshi.Json

data class UserPresenceResponse(
    @Json(name = "presence")
    val presence: Presence
)