package com.example.tinkoff_chat_app.domain.repository.messages_repository

import android.util.Log
import com.example.tinkoff_chat_app.data.messages.MessageDao
import com.example.tinkoff_chat_app.models.data_transfer_models.MessageDto
import com.example.tinkoff_chat_app.network.ChatApi
import com.example.tinkoff_chat_app.network.narrow.NarrowItem
import com.example.tinkoff_chat_app.utils.LocalData.MESSAGES_TO_SAVE
import com.example.tinkoff_chat_app.utils.MessagesMappers.toDatabaseMessageModelList
import com.example.tinkoff_chat_app.utils.MessagesMappers.toMessageDtoList
import com.example.tinkoff_chat_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class MessagesRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val chatDao: MessageDao,
) : MessagesRepository {

    override val currentMessages: MutableStateFlow<Resource<List<MessageDto>>> = MutableStateFlow(
        Resource.Loading()
    )
    private val messages
        get() = currentMessages.value

    override suspend fun loadMessagesWhenStart(
        topicName: String,
        streamName: String,
        amount: Int,
        lastMsgId: Int?,
        shouldFetch: Boolean
    ) {
        val data = chatDao.getMessages(
            fromStream = streamName,
            fromTopic = topicName
        ).toMessageDtoList()

        if (data.isNotEmpty())
            currentMessages.emit(
                Resource.Success(data)
            )

        if (shouldFetch) {

            if (data.isEmpty())
                currentMessages.emit(
                    Resource.Loading()
                )

            try {
                val newMessagesNetwork = loadMessagesByTopicNetwork(
                    topicName = topicName,
                    streamName = streamName,
                    lastMsgId = lastMsgId,
                    amount = amount
                )
                loadFirstMessages(newMessagesNetwork)
                updateDatabase(streamName, topicName)
            } catch (e: Exception) {
                if (data.isEmpty())
                    currentMessages.emit(
                        Resource.Error(error = e)
                    )
                else
                    currentMessages.emit(
                        Resource.Success(data = data)
                    )
            }
        } else if (data.isEmpty())
            currentMessages.emit(
                Resource.Error(error = IllegalStateException())
            )
        else
            currentMessages.emit(
                Resource.Success(data = data)
            )
    }

    override suspend fun loadNewMessages(
        topicName: String,
        streamName: String,
        amount: Int,
        lastMsgId: Int?,
    ) {
        val networkMessages = loadMessagesByTopicNetwork(
            topicName = topicName,
            streamName = streamName,
            lastMsgId = lastMsgId,
            amount = amount
        )
        addNewMessagesToCurrentMessages(networkMessages)
        updateDatabase(streamName, topicName)
    }

    private suspend fun loadFirstMessages(newMessagesNetwork: List<MessageDto>) {
        currentMessages.emit(
            Resource.Success(
                data = newMessagesNetwork
            )
        )
        Log.d("TAGTAGTAG", "LOAD FIRST MESSAGES")
    }

    private suspend fun addNewMessagesToCurrentMessages(newMessages: List<MessageDto>) {
        val messages = (messages as Resource.Success).data.toMutableList()
        val messagesToAdd = newMessages.filter { it !in messages }
        messages.addAll(0, messagesToAdd)
        currentMessages.emit(
            Resource.Success(
                data = messages
            )
        )
        Log.d("TAGTAGTAG", "ADD NEW MESSAGES")
    }

    private suspend fun updateDatabase(
        streamName: String,
        topicName: String,
        amountToSave: Int = MESSAGES_TO_SAVE
    ) {
        val messagesToSave =
            (messages as Resource.Success).data.takeLast(amountToSave).toMutableList()
                .toDatabaseMessageModelList(streamName, topicName)
        val currentDBMessages = chatDao.getMessages(topicName, streamName)
        val finalMessagesToSave = messagesToSave + currentDBMessages.drop(messagesToSave.size)

        chatDao.deleteMessagesOfCurrentTopic(fromTopic = topicName, fromStream = streamName)
        chatDao.insertAll(finalMessagesToSave)
    }

    private suspend fun loadMessagesByTopicNetwork(
        topicName: String, streamName: String, amount: Int, lastMsgId: Int?
    ): List<MessageDto> {
        val anchor = lastMsgId?.toString() ?: "newest"
        val narrow = listOf(
            NarrowItem(
                operand = streamName, operator = "stream"
            ), NarrowItem(
                operand = topicName, operator = "topic"
            )
        )

        return (chatApi.getMessages(
            anchor = anchor, narrow = narrow.toString(), numBefore = amount, numAfter = 0
        ).body()?.messages ?: emptyList()).toMessageDtoList()
    }

    override suspend fun sendMessage(
        topicName: String, content: String, streamId: String
    ) = chatApi.sendMessage(
        to = streamId, content = content, topic = topicName
    )

    override suspend fun sendReaction(
        msgId: String, emojiName: String
    ) = chatApi.sendReaction(
        msgId = msgId, emojiName = emojiName
    )

    override suspend fun removeReaction(
        msgId: String, emojiName: String
    ) = chatApi.removeReaction(
        msgId = msgId, emojiName = emojiName
    )

    suspend fun reloadMessages(
        streamName: String,
        topicName: String,
    ) {
        val anchor = "newest"
        val narrow = listOf(
            NarrowItem(
                operand = streamName, operator = "stream"
            ), NarrowItem(
                operand = topicName, operator = "topic"
            )
        )

        val networkMessages = (chatApi.getMessages(
            anchor = anchor, narrow = narrow.toString(), numBefore = 1000, numAfter = 0
        ).body()?.messages ?: emptyList()).toMessageDtoList()

        Log.d("TAGTAGTAGTAG", "${networkMessages.map { it.msg + "\n" }}")

        if (networkMessages.isNotEmpty())
            currentMessages.emit(
                Resource.Success(
                    data = networkMessages
                )
            )
    }

}