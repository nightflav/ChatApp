package com.example.tinkoff_chat_app.domain.repository.messages_repository

import com.example.tinkoff_chat_app.network.network_models.messages.Message

interface MessagesRepository {

    suspend fun getMessagesByTopic(
        topicName: String,
        streamName: String
    ): List<Message>

    suspend fun sendMessage(
        topicName: String,
        content: String,
        streamId: String
    )

    suspend fun sendReaction(
        msgId: String,
        emojiName: String
    )

    suspend fun removeReaction(
        msgId: String,
        emojiName: String
    )

}