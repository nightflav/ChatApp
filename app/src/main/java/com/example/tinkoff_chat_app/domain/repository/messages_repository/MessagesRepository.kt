package com.example.tinkoff_chat_app.domain.repository.messages_repository

import com.example.tinkoff_chat_app.models.data_transfer_models.MessageDto
import com.example.tinkoff_chat_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow

interface MessagesRepository {

    val currentMessages: MutableStateFlow<Resource<List<MessageDto>>>

    suspend fun loadMessagesWhenStart(
        topicName: String?,
        streamName: String,
        amount: Int,
        lastMsgId: Int?,
        shouldFetch: Boolean
    )

    suspend fun loadNewMessages(
        topicName: String?,
        streamName: String,
        amount: Int,
        lastMsgId: Int?,
    )

    suspend fun registerQueue(
        topicName: String?,
        streamName: String,
    ): Map<String, String>

    suspend fun getEventsFromQueue(
        queue: Map<String, String>
    ): String

    suspend fun sendMessage(
        topicName: String,
        content: String,
        streamId: Int
    )

    suspend fun sendReaction(
        msgId: Int,
        emojiName: String
    )

    suspend fun removeReaction(
        msgId: Int,
        emojiName: String
    )

    suspend fun deleteMessage(
        msgId: Int
    )

    suspend fun updateMessageContent(
        msgId: Int,
        newContent: String
    )

    suspend fun updateMessageTopic(
        msgId: Int,
        newTopic: String
    )

    suspend fun fetchMessage(
        msgId: Int,
        allTopics: Boolean
    )

    suspend fun uploadFile(
        file: ByteArray,
        fileName: String
    ): String?
}