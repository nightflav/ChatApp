package com.example.tinkoff_chat_app.models.data_transfer_models

data class MessageDto(
    val avatarUri: String,
    val messageId: Int,
    val senderName: String,
    val senderId: Int,
    val msg: String,
    val reactions: List<ReactionDto>,
    val date: Int,
    val topic: String
)