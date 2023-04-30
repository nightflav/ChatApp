package com.example.tinkoff_chat_app.models.data_transfer_models

data class ReactionDto(
    val emojiCode: String,
    val emojiName: String,
    val userId: Int
)
