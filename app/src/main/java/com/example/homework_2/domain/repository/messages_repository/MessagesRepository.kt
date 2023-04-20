package com.example.homework_2.domain.repository.messages_repository

import com.example.homework_2.screens.message.MessagesScreenState
import kotlinx.coroutines.flow.Flow

interface MessagesRepository {

    suspend fun getMessagesByTopic(
        topicName: String,
        streamName: String
    ): Flow<MessagesScreenState>

    suspend fun sendMessage(
        topicName: String,
        content: String,
        streamId: String
    ): Flow<MessagesScreenState>

    suspend fun sendReaction(
        msgId: String,
        emojiName: String
    ): Flow<MessagesScreenState>

    suspend fun removeReaction(
        msgId: String,
        emojiName: String
    ): Flow<MessagesScreenState>

}