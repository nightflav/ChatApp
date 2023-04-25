package com.example.tinkoff_chat_app.domain.repository.messages_repository

import com.example.tinkoff_chat_app.data.messages.DatabaseMessageModel
import com.example.tinkoff_chat_app.data.messages.MessageDao
import com.example.tinkoff_chat_app.network.ChatApi
import com.example.tinkoff_chat_app.network.narrow.NarrowItem
import com.example.tinkoff_chat_app.network.network_models.messages.Message
import com.example.tinkoff_chat_app.network.network_models.messages.Reaction
import javax.inject.Inject

class MessagesRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val chatDao: MessageDao
) : MessagesRepository {

    private val messagesByTopic: MutableList<MessageDto> = mutableListOf()

    override suspend fun getMessagesByTopicLocal(
        topicName: String,
        streamName: String,
    ): List<MessageDto> {
        messagesByTopic.clear()
        return chatDao.readAllMessages(
            fromStream = streamName,
            fromTopic = topicName
        ).toMessageDtoList()
    }

    override suspend fun saveMessages(
        topicName: String,
        streamName: String,
        maxMessagesToSave: Int
    ) {
        val messagesToSave = messagesByTopic.takeLast(maxMessagesToSave).toMutableList()
            .toDatabaseMessageModelList(streamName, topicName)
        val currentDBMessages = chatDao.readAllMessages(topicName, streamName)
        val finalMessagesToSave = messagesToSave + currentDBMessages.drop(messagesToSave.size)
        for (msg in currentDBMessages.take(messagesToSave.size))
            chatDao.deleteMessage(msg)
        for (message in finalMessagesToSave) {
            chatDao.addMessage(message = message)
        }
    }

    override suspend fun getMessagesByTopicNetwork(
        topicName: String,
        streamName: String,
        amount: Int,
        lastMsgId: Int?
    ): List<MessageDto> {
        loadMessagesByTopicNetwork(topicName, streamName, amount, lastMsgId)
        return messagesByTopic
    }

    private suspend fun loadMessagesByTopicNetwork(
        topicName: String,
        streamName: String,
        amount: Int,
        lastMsgId: Int?
    ) {
        val anchor = lastMsgId?.toString() ?: "newest"
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
        val networkMessages = (chatApi.getMessages(
            anchor = anchor,
            narrow = narrow.toString(),
            numBefore = amount,
            numAfter = 0
        ).body()?.messages ?: emptyList()).mapMessageDtoList()

        messagesByTopic.addAll(0, networkMessages)
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

    private fun List<Message>.mapMessageDtoList() = this.map {
        MessageDto(
            senderName = it.senderFullName,
            senderId = it.senderId,
            reactions = it.reactions.toReactionsDtoList(),
            messageId = it.id,
            date = it.timestamp,
            msg = it.content
        )
    }

    private fun List<Reaction>.toReactionsDtoList() = this.map {
        ReactionDto(
            emojiName = it.emojiName,
            emojiCode = it.emojiCode,
            userId = it.userId
        )
    }

    private fun List<DatabaseMessageModel>.toMessageDtoList() = this.map {
        MessageDto(
            senderName = it.senderName,
            senderId = it.senderId,
            reactions = it.reactions,
            messageId = it.id,
            date = it.date,
            msg = it.msg
        )
    }

    private fun List<MessageDto>.toDatabaseMessageModelList(
        streamName: String,
        topicName: String
    ) = this.map {
        DatabaseMessageModel(
            senderName = it.senderName,
            senderId = it.senderId,
            reactions = it.reactions,
            id = it.messageId,
            date = it.date,
            msg = it.msg,
            streamName = streamName,
            topicName = topicName,
            topicStreamId = topicName + streamName
        )
    }

    suspend fun reloadMessages(
        streamName: String,
        topicName: String,
    ) {
        val anchor = "newest"
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
        val amount = messagesByTopic.size
        val networkMessages = (chatApi.getMessages(
            anchor = anchor,
            narrow = narrow.toString(),
            numBefore = amount,
            numAfter = 0
        ).body()?.messages ?: emptyList()).mapMessageDtoList()

        messagesByTopic.clear()
        messagesByTopic.addAll(networkMessages)
    }

}