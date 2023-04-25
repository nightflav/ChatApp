package com.example.tinkoff_chat_app.domain.repository.messages_repository

data class ReactionDto(
    val emojiCode: String,
    val emojiName: String,
    val userId: Int
)
