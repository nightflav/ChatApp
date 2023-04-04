package com.example.homework_2.network.networkModels.streams

data class StreamsResponse(
    val msg: String,
    val result: String,
    val streams: List<Stream>
)