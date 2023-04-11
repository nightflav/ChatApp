package com.example.homework_2.network.networkModels.users.presence

import com.squareup.moshi.Json

data class Presence(
    @Json(name = "aggregated")
    val aggregated: Aggregated
)