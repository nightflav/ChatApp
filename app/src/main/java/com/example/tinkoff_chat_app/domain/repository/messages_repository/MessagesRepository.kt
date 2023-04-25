package com.example.tinkoff_chat_app.domain.repository.messages_repository

interface MessagesRepository {

    suspend fun getMessagesByTopicLocal(
        topicName: String,
        streamName: String,
    ): List<MessageDto>?

    suspend fun saveMessages(
        topicName: String,
        streamName: String,
        maxMessagesToSave: Int
    )

    suspend fun getMessagesByTopicNetwork(
        topicName: String,
        streamName: String,
        amount: Int,
        lastMsgId: Int?
    ): List<MessageDto>

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