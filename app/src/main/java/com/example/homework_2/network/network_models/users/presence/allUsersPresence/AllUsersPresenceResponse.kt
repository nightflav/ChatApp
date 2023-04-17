package com.example.homework_2.network.network_models.users.presence.allUsersPresence

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AllUsersPresenceResponse(
    @Json (name = "presences")
    val presences: Map<String, UserPresenceSource>,
)
