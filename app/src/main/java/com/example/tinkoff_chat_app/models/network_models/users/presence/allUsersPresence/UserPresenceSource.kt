package com.example.tinkoff_chat_app.models.network_models.users.presence.allUsersPresence

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserPresenceSource(
    val aggregated: Aggregated,
    val website: Website
)