package com.example.homework_2.network.network_models.subscriptions

import com.squareup.moshi.Json

data class Subscription(
    @Json(name = "name")
    val name: String,
    @Json(name = "stream_id")
    val streamId: Int,
)