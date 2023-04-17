package com.example.homework_2.network.network_models.topics

import com.squareup.moshi.Json

data class Topic(
    @Json(name = "name")
    val name: String
)