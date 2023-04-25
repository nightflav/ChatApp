package com.example.tinkoff_chat_app.domain.repository.messages_repository

data class MessageDto(
    val senderName: String,
    val msg: String,
    val reactions: List<ReactionDto>,
    val senderId: Int,
    val messageId: Int,
    val date: Int
)