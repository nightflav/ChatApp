package com.example.tinkoff_chat_app.models.data_transfer_models

data class TopicDto(
    val id: String,
    val name: String,
    var msgCount: Int,
    val parentId: Int,
    val parentName: String,
    val isLoading: Boolean
)