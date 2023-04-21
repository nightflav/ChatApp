package com.example.tinkoff_chat_app.models.stream_screen_models

data class StreamModel  (
    val name: String = "",
    var isSelected: Boolean = false,
    val topics: List<TopicModel>?,
    val id: String
): StreamScreenItem()