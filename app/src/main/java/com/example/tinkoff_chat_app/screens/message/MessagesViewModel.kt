package com.example.tinkoff_chat_app.screens.message

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepository
import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepositoryImpl
import com.example.tinkoff_chat_app.domain.usecases.messages.ChangeReactionSelectedStateUseCase
import com.example.tinkoff_chat_app.domain.usecases.messages.SendMessageUseCase
import com.example.tinkoff_chat_app.domain.usecases.messages.SubscribeForMessagesUseCase
import com.example.tinkoff_chat_app.models.ui_models.MessageReaction
import com.example.tinkoff_chat_app.screens.message.MessagesIntents.*
import com.example.tinkoff_chat_app.utils.Network.MESSAGES_TO_LOAD
import com.example.tinkoff_chat_app.utils.Resource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
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

    private val currState
        get() = screenState.value

    private val topicName
        get() = currState.topic!!

    private val streamName
        get() = currState.stream!!

    private val streamId
        get() = currState.streamId!!

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
                                    streamId = it.streamName,
                                    stream = it.streamName,
                                    topic = it.topicName
                                )
                            )
                            subscribeForMessages()
                            messageRepo.loadMessagesWhenStart(
                                topicName = it.topicName,
                                streamName = it.streamName,
                                amount = MESSAGES_TO_LOAD,
                                lastMsgId = null,
                                shouldFetch = true
                            )
                        }
                        is UpdateMessagesIntent -> updateMessages()
                        is SendMessageIntent -> sendMessage(
                            content = it.content,
                        )
                        is ChangeReactionStateIntent -> changeReactionState(
                            reaction = it.reaction,
                            msgId = it.msgId
                        )
                        is LoadMessagesIntent -> messageRepo.loadNewMessages(
                            topicName,
                            streamName,
                            it.amount,
                            it.lastMsgId,
                        )
                    }
                }
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun subscribeForMessages() {
        viewModelScope.launch {
            subscribeForMessagesUseCase()
                .debounce(50L)
                .collect {
                Log.d("TAGTAGTAG", "COLLECTED")
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

    private suspend fun changeReactionState(reaction: MessageReaction, msgId: String) {
        val state = try {
            changeReactionSelectedStateUseCase(reaction, msgId)
            currState
        } catch (e: Exception) {
            currState.copy(
                isLoading = false,
                error = e
            )
        }
        _screenState.emit(state)
    }

    private suspend fun sendMessage(content: String) {
        val state = try {
            sendMessageUseCase(
                content = content,
                topicName = topicName,
                streamId = streamId
            )
            currState
        } catch (e: Exception) {
            currState.copy(
                error = e,
                isLoading = false
            )
        }
        _screenState.emit(state)
    }

    private suspend fun updateMessages() {
        (messageRepo as MessagesRepositoryImpl).reloadMessages(streamName, topicName)
    }
}


