package com.example.tinkoff_chat_app.domain.repository.messages_repository

import com.example.tinkoff_chat_app.network.ChatApi
import com.example.tinkoff_chat_app.network.narrow.NarrowItem
import com.example.tinkoff_chat_app.network.network_models.messages.Message
import javax.inject.Inject

class MessagesRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi
) : MessagesRepository {

    private val messagesByTopic: MutableMap<String, List<Message>?> = mutableMapOf()

    override suspend fun getMessagesByTopic(
        topicName: String,
        streamName: String
    ): List<Message> = if (messagesByTopic[topicName] == null) {
        loadMessagesByTopic(topicName, streamName)
        messagesByTopic[topicName]!!
    } else {
        messagesByTopic[topicName]!!
    }

    private suspend fun loadMessagesByTopic(
        topicName: String,
        streamName: String
    ) {
        val narrow = listOf(
            NarrowItem(
                operand = streamName,
                operator = "stream"
            ),
            NarrowItem(
                operand = topicName,
                operator = "topic"
            )
        )
        val networkMessages = chatApi.getMessages(
            narrow = narrow.toString(),
            numBefore = 1000,
            numAfter = 0
        ).body()?.messages

        messagesByTopic[topicName] = networkMessages
    }

    override suspend fun sendMessage(
        topicName: String,
        content: String,
        streamId: String
    ) = chatApi.sendMessage(
        to = streamId,
        content = content,
        topic = topicName
    )

    override suspend fun sendReaction(
        msgId: String,
        emojiName: String
    ) = chatApi.sendReaction(
        msgId = msgId,
        emojiName = emojiName
    )

    override suspend fun removeReaction(
        msgId: String,
        emojiName: String
    ) = chatApi.removeReaction(
        msgId = msgId,
        emojiName = emojiName
    )
}