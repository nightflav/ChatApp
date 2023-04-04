package com.example.homework_2.network.networkModels.messages

data class MessageResponse(
    val anchor: Long,
    val found_anchor: Boolean,
    val found_newest: Boolean,
    val messages: List<Message>,
    val msg: String,
    val result: String
)