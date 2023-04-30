package com.example.tinkoff_chat_app.domain.repository.messages_repository

import com.example.tinkoff_chat_app.models.data_transfer_models.MessageDto
import com.example.tinkoff_chat_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow


interface MessagesRepository {

    val currentMessages: MutableStateFlow<Resource<List<MessageDto>>>

    suspend fun loadMessagesWhenStart(
        topicName: String,
        streamName: String,
        amount: Int,
        lastMsgId: Int?,
        shouldFetch: Boolean
    )

    suspend fun loadNewMessages(
        topicName: String,
        streamName: String,
        amount: Int,
        lastMsgId: Int?,
    )

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