package com.example.tinkoff_chat_app.models.stream_screen_models

data class TopicModel(
    val name: String = "",
    var msgCount: Int = 0,
    val parentId: String,
    val id: String,
    val parentName: String,
    val isLoading: Boolean = false
) : StreamScreenItem()