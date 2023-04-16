package com.example.homework_2.repository.messagesRepository

import com.example.homework_2.models.SingleMessage
import com.example.homework_2.network.RetrofitInstance.Companion.chatApi
import com.example.homework_2.network.narrow.NarrowItem
import com.example.homework_2.repository.profileRepository.ProfileRepositoryImpl
import com.example.homework_2.repository.streamsRepository.StreamsRepositoryImpl
import com.example.homework_2.screens.message.MessagesScreenState
import com.example.homework_2.utils.MessageTypes
import com.example.homework_2.utils.addDateSeparators
import com.example.homework_2.utils.toSingleMessageList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MessageRepositoryImpl : MessagesRepository {

    private val messagesByTopic: MutableMap<String, List<SingleMessage>?> = mutableMapOf()

    override suspend fun getMessagesByTopic(
        topicName: String,
        streamName: String
    ): Flow<MessagesScreenState> = flow {
        emit(MessagesScreenState.Loading)
        if (messagesByTopic[topicName]?.isEmpty() == true || messagesByTopic[topicName] == null) {
            try {
                loadMessagesByTopic(topicName, streamName)
                emit(MessagesScreenState.Success(messagesByTopic[topicName]!!))
            } catch (e: Exception) {
                emit(MessagesScreenState.Error)
            }
        } else {
            emit(MessagesScreenState.Success(messagesByTopic[topicName]!!))
        }
    }

    suspend fun loadMessagesByTopic(
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
            numBefore = 100,
            numAfter = 0
        ).body()?.messages?.toSingleMessageList()
        StreamsRepositoryImpl().setTopicMsgCount(topicName, networkMessages?.size ?: 0, streamName)
        messagesByTopic[topicName] = networkMessages.addDateSeparators()
    }

    override suspend fun sendMessage(
        topicName: String,
        content: String,
        streamId: String
    ): Flow<MessagesScreenState> = flow {
        try {
            chatApi.sendMessage(
                to = streamId,
                content = content,
                topic = topicName
            )
            emit(MessagesScreenState.Success(messagesByTopic[topicName]!!))
        } catch (e: Exception) {
            emit(MessagesScreenState.Error)
        }
    }

    override suspend fun sendReaction(
        msgId: String,
        emojiName: String
    ): Flow<MessagesScreenState> = flow {
        try {
            chatApi.sendReaction(
                msgId = msgId,
                emojiName = emojiName
            )
            emit(MessagesScreenState.Init)
        } catch (e: Exception) {
            emit(MessagesScreenState.Error)
        }
    }

    override suspend fun removeReaction(
        msgId: String,
        emojiName: String
    ): Flow<MessagesScreenState> = flow {
        try {
            chatApi.removeReaction(
                msgId = msgId,
                emojiName = emojiName
            )
            emit(MessagesScreenState.Init)
        } catch (e: Exception) {
            emit(MessagesScreenState.Error)
        }
    }

    suspend fun getMsgTypeId(senderId: Int): String =
        when (ProfileRepositoryImpl().getProfile().id) {
            senderId -> MessageTypes.SENDER
            else -> MessageTypes.RECEIVER
        }
}