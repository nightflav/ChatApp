package com.example.homework_2.network.network_models.users.presence.allUsersPresence

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserPresenceSource(
    val aggregated: Aggregated,
    val website: Website
)