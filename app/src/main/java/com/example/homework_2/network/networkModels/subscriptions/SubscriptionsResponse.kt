package com.example.homework_2.network.networkModels.subscriptions

data class SubscriptionsResponse(
    val msg: String,
    val result: String,
    val subscriptions: List<Subscription>
)