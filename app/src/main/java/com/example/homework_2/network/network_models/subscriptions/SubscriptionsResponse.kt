package com.example.homework_2.network.network_models.subscriptions

import com.squareup.moshi.Json

data class SubscriptionsResponse(
    @Json(name = "subscriptions")
    val subscriptions: List<Subscription>
)