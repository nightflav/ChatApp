package com.example.homework_2.network.networkModels.topics

data class TopicsResponse(
    val msg: String,
    val result: String,
    val topics: List<Topic>
)