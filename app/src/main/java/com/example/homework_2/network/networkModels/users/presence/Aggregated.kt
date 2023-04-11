package com.example.homework_2.network.networkModels.users.presence

import com.squareup.moshi.Json

data class Aggregated(
    @Json(name = "status")
    val status: String,
)