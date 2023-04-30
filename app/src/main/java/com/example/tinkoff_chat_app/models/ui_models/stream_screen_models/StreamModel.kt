package com.example.tinkoff_chat_app.models.ui_models.stream_screen_models

data class StreamModel(
    val name: String = "",
    var isSelected: Boolean = false,
    val id: Int,
    val topics: List<TopicModel>? = null
) : StreamScreenItem()