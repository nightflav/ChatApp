package com.example.homework_2.network.networkModels.users.presence

import com.squareup.moshi.Json

data class UserPresenceResponse(
    @Json(name = "presence")
    val presence: Presence
)