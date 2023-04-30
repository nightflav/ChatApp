package com.example.tinkoff_chat_app.models.data_transfer_models

data class StreamDto(
    val id: Int,
    val name: String,
    val isSelected: Boolean,
    val isSubscriptionStream: Boolean = false,
    val topics: List<TopicDto>?
)