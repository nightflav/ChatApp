package com.example.homework_2.network.network_models.topics

import com.squareup.moshi.Json

data class TopicsResponse(
    @Json(name = "topics")
    val topics: List<Topic>
)