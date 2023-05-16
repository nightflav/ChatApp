package com.example.tinkoff_chat_app.domain.repository.messages_repository

import com.example.tinkoff_chat_app.data.messages.MessageDao
import com.example.tinkoff_chat_app.models.data_transfer_models.MessageDto
import com.example.tinkoff_chat_app.models.data_transfer_models.ReactionDto
import com.example.tinkoff_chat_app.models.network_models.events_queue.messages.MessageScreenEvent
import com.example.tinkoff_chat_app.models.network_models.messages.Message
import com.example.tinkoff_chat_app.network.ChatApi
import com.example.tinkoff_chat_app.utils.LocalData.MESSAGES_TO_SAVE
import com.example.tinkoff_chat_app.utils.MessagesMappers.toDatabaseMessageModelList
import com.example.tinkoff_chat_app.utils.MessagesMappers.toMessageDto
import com.example.tinkoff_chat_app.utils.MessagesMappers.toMessageDtoList
import com.example.tinkoff_chat_app.utils.RealTimeEvents.LAST_EVENT_ID_KEY
import com.example.tinkoff_chat_app.utils.RealTimeEvents.QUEUE_ID_KEY
import com.example.tinkoff_chat_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
        topicName: String?, streamName: String, amount: Int, lastMsgId: Int?, shouldFetch: Boolean
    ) {
        val lowerTopicName = topicName?.lowercase()
        if (lowerTopicName != null) {
            loadMessagesWhenStartWithTopic(lowerTopicName, streamName, amount, lastMsgId, shouldFetch)
        } else {
            loadMessagesWhenStartWithoutTopic(streamName, amount, lastMsgId, shouldFetch)
        }
    }

    private suspend fun loadMessagesWhenStartWithoutTopic(
        streamName: String,
        amount: Int,
        lastMsgId: Int?,
        shouldFetch: Boolean
    ) {
        val data = chatDao.getMessagesFromStream(
            fromStream = streamName
        ).toMessageDtoList()
        if (data.isNotEmpty()) currentMessages.emit(
            Resource.Success(data)
        )
        if (shouldFetch) {
            if (data.isEmpty()) currentMessages.emit(
                Resource.Loading()
            )
            try {
                val newMessagesNetwork = loadMessagesByNetworkFromStream(
                    streamName = streamName,
                    lastMsgId = lastMsgId,
                    amount = amount
                )
                loadFirstMessages(newMessagesNetwork)
                updateStreamDatabase(streamName)
            } catch (e: Exception) {
                if (data.isEmpty()) currentMessages.emit(
                    Resource.Error(error = e)
                )
                else currentMessages.emit(
                    Resource.Success(data = data)
                )
            }
        } else if (data.isEmpty()) currentMessages.emit(
            Resource.Error(error = IllegalStateException())
        )
        else currentMessages.emit(
            Resource.Success(data = data)
        )
    }

    private suspend fun loadMessagesWhenStartWithTopic(
        topicName: String,
        streamName: String,
        amount: Int,
        lastMsgId: Int?,
        shouldFetch: Boolean
    ) {
        val data = chatDao.getMessages(
            fromStream = streamName, fromTopic = topicName
        ).toMessageDtoList()
        if (data.isNotEmpty()) currentMessages.emit(
            Resource.Success(data)
        )
        if (shouldFetch) {
            if (data.isEmpty()) currentMessages.emit(
                Resource.Loading()
            )
            try {
                val newMessagesNetwork = loadMessagesByNetwork(
                    topicName = topicName,
                    streamName = streamName,
                    lastMsgId = lastMsgId,
                    amount = amount
                )
                loadFirstMessages(newMessagesNetwork)
                updateDatabase(streamName, topicName)
            } catch (e: Exception) {
                if (data.isEmpty()) currentMessages.emit(
                    Resource.Error(error = e)
                )
                else currentMessages.emit(
                    Resource.Success(data = data)
                )
            }
        } else if (data.isEmpty()) currentMessages.emit(
            Resource.Error(error = IllegalStateException())
        )
        else currentMessages.emit(
            Resource.Success(data = data)
        )
    }

    override suspend fun loadNewMessages(
        topicName: String?,
        streamName: String,
        amount: Int,
        lastMsgId: Int?,
    ) {
        val lowerTopicName = topicName?.lowercase()
        if (lowerTopicName == null) {
            val networkMessages = loadMessagesByNetworkFromStream(
                streamName = streamName,
                lastMsgId = lastMsgId,
                amount = amount
            )
            addNewMessagesToCurrentMessages(networkMessages)
            updateStreamDatabase(streamName)
        } else {
            val networkMessages = loadMessagesByNetwork(
                topicName = lowerTopicName,
                streamName = streamName,
                lastMsgId = lastMsgId,
                amount = amount
            )
            addNewMessagesToCurrentMessages(networkMessages)
            updateDatabase(streamName, lowerTopicName)
        }
    }

    private suspend fun loadFirstMessages(newMessagesNetwork: List<MessageDto>) {
        currentMessages.emit(
            Resource.Success(
                data = newMessagesNetwork
            )
        )
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
    }

    private suspend fun updateDatabase(
        streamName: String, topicName: String, amountToSave: Int = MESSAGES_TO_SAVE
    ) {
        val messagesToSave =
            (messages as Resource.Success).data.takeLast(amountToSave).toMutableList()
                .toDatabaseMessageModelList(streamName)
        val currentDBMessages = chatDao.getMessages(topicName, streamName)
        val finalMessagesToSave = messagesToSave + currentDBMessages.drop(messagesToSave.size)

        chatDao.deleteMessagesOfCurrentTopic(fromTopic = topicName, fromStream = streamName)
        chatDao.insertAll(finalMessagesToSave)
    }

    private suspend fun updateStreamDatabase(
        streamName: String, amountToSave: Int = MESSAGES_TO_SAVE
    ) {
        val messagesToSave =
            (messages as Resource.Success).data.takeLast(amountToSave).toMutableList()
                .toDatabaseMessageModelList(streamName)
        val currentDBMessages = chatDao.getMessagesFromStream(streamName)
        val finalMessagesToSave = messagesToSave + currentDBMessages.drop(messagesToSave.size)

        chatDao.deleteMessagesOfCurrentStream(fromStream = streamName)
        chatDao.insertAll(finalMessagesToSave)
    }

    private suspend fun loadMessagesByNetwork(
        topicName: String, streamName: String, amount: Int, lastMsgId: Int?
    ): List<MessageDto> {
        val anchor = lastMsgId?.toString() ?: "newest"
        val narrow = "[{ \"operator\": \"stream\",\"operand\": \"$streamName\" }," +
                "{ \"operator\": \"topic\",\"operand\": \"$topicName\" }]"
        return (chatApi.getMessages(
            anchor = anchor, narrow = narrow, numBefore = amount, numAfter = 0
        ).body()?.messages ?: emptyList()).toMessageDtoList()
    }

    private suspend fun loadMessagesByNetworkFromStream(
        streamName: String, amount: Int, lastMsgId: Int?
    ): List<MessageDto> {
        val anchor = lastMsgId?.toString() ?: "newest"
        val narrow = "[{ \"operator\": \"stream\",\"operand\": \"$streamName\" }]"
        return (chatApi.getMessages(
            anchor = anchor, narrow = narrow, numBefore = amount, numAfter = 0
        ).body()?.messages ?: emptyList()).toMessageDtoList()
    }

    override suspend fun deleteMessage(msgId: Int) = chatApi.deleteMessage(msgId)

    override suspend fun sendMessage(
        topicName: String, content: String, streamId: String
    ) = chatApi.sendMessage(
        to = streamId, content = content, topic = topicName
    )

    override suspend fun sendReaction(
        msgId: Int, emojiName: String
    ) = chatApi.sendReaction(
        msgId = msgId, emojiName = emojiName
    )

    override suspend fun removeReaction(
        msgId: Int, emojiName: String
    ) = chatApi.removeReaction(
        msgId = msgId, emojiName = emojiName
    )

    override suspend fun registerQueue(
        topicName: String?, streamName: String
    ): Map<String, String> {
        val narrow = if (topicName != null)
            "[[\"stream\", \"$streamName\"], [\"topic\", \"$topicName\"]]"
        else
            "[[\"stream\", \"$streamName\"]]"
        val eventType = "[\"message\", \"reaction\", \"delete_message\"]"
        val events =
            chatApi.registerQueue(narrow, eventType).body()
        return mapOf(
            QUEUE_ID_KEY to events!!.queueId, LAST_EVENT_ID_KEY to events.lastEventId.toString()
        )
    }

    override suspend fun getEventsFromQueue(queue: Map<String, String>): String {
        val queueId = queue[QUEUE_ID_KEY]!!
        val lastEventId = queue[LAST_EVENT_ID_KEY]!!
        val events =
            chatApi.getMessageEvents(queueId = queueId, lastEventId = lastEventId)
                .body()?.messageScreenEvents
        if (events != null) {
            for (event in events) {
                when (event.type) {
                    "message" -> {
                        insertMessage(event.message!!)
                    }
                    "delete_message" -> {
                        removeMessage(event.messageId!!)
                    }
                    "reaction" -> {
                        renewReaction(
                            ReactionDto(
                                emojiCode = event.emojiCode!!,
                                emojiName = event.emojiName!!,
                                userId = event.userId!!
                            ),
                            messageId = event.messageId!!,
                        )
                    }
                    "update_message" -> {
                        updateMessage(event.message!!, false)
                    }
                    else -> throw IllegalStateException()
                }
            }
        }
        return (events?.last() as MessageScreenEvent).id.toString()
    }

    private fun insertMessage(message: Message) {
        val newMessages = (currentMessages.value as Resource.Success).data.toMutableList()
        newMessages.add(message.toMessageDto())
        currentMessages.value = (currentMessages.value as Resource.Success<List<MessageDto>>).copy(
            data = newMessages
        )
    }

    private fun removeMessage(messageId: Int) {
        val newMessages = (currentMessages.value as Resource.Success).data.toMutableList()
        val messageToRemove = newMessages.first { it.messageId == messageId }
        newMessages.remove(messageToRemove)
        currentMessages.value = (currentMessages.value as Resource.Success<List<MessageDto>>).copy(
            data = newMessages
        )
    }

    private fun renewReaction(reaction: ReactionDto, messageId: Int) {
        val currMessages = (currentMessages.value as Resource.Success).data.toMutableList()
        for (i in currMessages.indices) {
            if (currMessages[i].messageId == messageId) {
                val toAdd = reaction !in currMessages[i].reactions
                currMessages[i] = currMessages[i].copy(
                    reactions = if (toAdd) currMessages[i].reactions + reaction else currMessages[i].reactions - reaction
                )
            }
        }
        currentMessages.value = (currentMessages.value as Resource.Success<List<MessageDto>>).copy(
            data = currMessages
        )
    }

    override suspend fun updateMessageContent(msgId: Int, newContent: String) =
        chatApi.updateMessageContent(msgId, newContent)

    override suspend fun updateMessageTopic(msgId: Int, newTopic: String) =
        chatApi.updateMessageTopic(msgId, newTopic)

    override suspend fun fetchMessage(msgId: Int, allTopics: Boolean) {
        val message = chatApi.fetchMessage(msgId).body() ?: return
        updateMessage(
            message = message.message.copy(
                content = message.message.content
            ), shouldDeleteWhenTopicChanges = allTopics
        )
    }

    private fun updateMessage(message: Message, shouldDeleteWhenTopicChanges: Boolean) {
        val currMessages =
            (currentMessages.value as Resource.Success<List<MessageDto>>).data.toMutableList()
        if (!shouldDeleteWhenTopicChanges && message.topic != currMessages.first { it.messageId == message.id }.topic) {
            removeMessage(messageId = message.id)
            return
        }
        currMessages.replaceAll { if (it.messageId == message.id) message.toMessageDto() else it }
        currentMessages.value = (currentMessages.value as Resource.Success<List<MessageDto>>).copy(
            data = currMessages
        )
    }

    override suspend fun uploadFile(file: ByteArray, fileName: String): String? =
        chatApi.uploadFile(
            body = MultipartBody.Part.createFormData(
                "content",
                fileName,
                file.toRequestBody()
            )
        ).body()?.uri
}
