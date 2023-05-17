package com.example.tinkoff_chat_app.screens.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepository
import com.example.tinkoff_chat_app.domain.usecases.messages.ChangeReactionSelectedStateUseCase
import com.example.tinkoff_chat_app.domain.usecases.messages.SendMessageUseCase
import com.example.tinkoff_chat_app.domain.usecases.messages.SubscribeForMessagesUseCase
import com.example.tinkoff_chat_app.models.ui_models.MessageReaction
import com.example.tinkoff_chat_app.screens.message.MessagesIntents.*
import com.example.tinkoff_chat_app.utils.Network.BASE_URL_FILES_UPLOAD
import com.example.tinkoff_chat_app.utils.Network.MESSAGES_TO_LOAD
import com.example.tinkoff_chat_app.utils.RealTimeEvents.LAST_EVENT_ID_KEY
import com.example.tinkoff_chat_app.utils.Resource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessagesViewModel @Inject constructor(
    private val subscribeForMessagesUseCase: SubscribeForMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val changeReactionSelectedStateUseCase: ChangeReactionSelectedStateUseCase,
    private val messageRepo: MessagesRepository
) : ViewModel() {

    val messagesChannel = Channel<MessagesIntents>()
    private val _screenState: MutableStateFlow<MessageScreenUiState> =
        MutableStateFlow(MessageScreenUiState())
    val screenState get() = _screenState.asStateFlow()
    private var isSubscribedForMessages: Boolean = false

    private val currState
        get() = screenState.value

    private val topicName
        get() = currState.topic

    private val streamName
        get() = currState.stream!!.name

    private val streamId
        get() = currState.stream!!.id

    private val allTopics
        get() = currState.allTopics

    init {
        subscribeForIntents()
    }

    private fun subscribeForIntents() {
        viewModelScope.launch {
            messagesChannel.consumeAsFlow()
                .collect {
                    when (it) {
                        is InitMessagesIntent -> {
                            _screenState.emit(
                                currState.copy(
                                    stream = it.stream,
                                    topic = it.topicName,
                                    allTopics = it.allTopics
                                )
                            )
                            if (!isSubscribedForMessages) {
                                subscribeForMessages()
                                viewModelScope.launch {
                                    subscribeForUpdates()
                                }
                                messageRepo.loadMessagesWhenStart(
                                    topicName = it.topicName,
                                    streamName = it.stream.name,
                                    amount = MESSAGES_TO_LOAD,
                                    lastMsgId = null,
                                    shouldFetch = true
                                )
                                isSubscribedForMessages = true
                            }
                        }
                        is SendMessageIntent -> sendMessage(
                            topic = it.topic,
                            content = it.content,
                            onError = it.onError
                        )
                        is ChangeReactionStateIntent -> changeReactionState(
                            reaction = it.reaction,
                            msgId = it.msgId
                        ) { message ->
                            it.onError(message)
                        }
                        is LoadMessagesIntent -> messageRepo.loadNewMessages(
                            topicName,
                            streamName,
                            it.amount,
                            it.lastMsgId,
                        )
                        is DeleteMessageIntent -> messageRepo.deleteMessage(it.msgId)
                        is EditMessageIntent -> try {
                            messageRepo.updateMessageContent(
                                it.msgId,
                                it.newMessageContent
                            )
                            messageRepo.fetchMessage(
                                it.msgId,
                                allTopics
                            )
                        } catch (e: Exception) {
                            it.onError(e.message ?: "An error occurred editing message.")
                        }
                        is ChangeMessageTopicIntent -> try {
                            messageRepo.updateMessageTopic(
                                it.msgId,
                                it.newTopicName
                            )
                            messageRepo.fetchMessage(
                                it.msgId,
                                allTopics
                            )
                        } catch (e: Exception) {
                            it.onError(e.message ?: "An error occurred changing topic.")
                        }
                        is UploadFileIntent -> try {
                            val fileUrl = messageRepo.uploadFile(
                                file = it.file,
                                fileName = it.fileName
                            )
                            sendMessageUseCase(
                                topicName = it.topic,
                                content = BASE_URL_FILES_UPLOAD + fileUrl!!,
                                streamId = streamId
                            )
                        } catch (e: Exception) {
                            it.onError("An error occurred loading file. $e")
                        }
                    }
                }
        }
    }

    private suspend fun subscribeForUpdates() {
        try {
            val queue =
                messageRepo.registerQueue(topicName, streamName).toMutableMap()
            while (true) {
                try {
                    val newEventId = messageRepo.getEventsFromQueue(
                        queue = queue
                    )
                    queue[LAST_EVENT_ID_KEY] = newEventId
                } catch (_: Exception) {
                }
                delay(500L)
            }
        } catch (_: Exception) {
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun subscribeForMessages() {
        viewModelScope.launch {
            subscribeForMessagesUseCase(allTopics)
                .debounce(50L)
                .collect {
                    when (it) {
                        is Resource.Error -> {
                            _screenState.emit(
                                currState.copy(
                                    error = it.error
                                )
                            )
                        }
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _screenState.emit(
                                currState.copy(
                                    messages = it.data
                                )
                            )
                        }
                    }
                }
        }
    }

    private suspend fun changeReactionState(
        reaction: MessageReaction,
        msgId: Int,
        onError: (String) -> Unit
    ) {
        try {
            changeReactionSelectedStateUseCase(reaction, msgId)
        } catch (_: Exception) {
            onError("An error occurred adding reaction.")
        }
    }

    private suspend fun sendMessage(topic: String, content: String, onError: () -> Unit) {
        try {
            sendMessageUseCase(
                content = content,
                topicName = topic,
                streamId = streamId
            )
            currState
        } catch (e: Exception) {
            onError()
        }
    }
}


