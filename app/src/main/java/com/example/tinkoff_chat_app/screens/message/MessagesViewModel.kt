package com.example.tinkoff_chat_app.screens.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tinkoff_chat_app.domain.usecases.messages.ChangeReactionSelectedStateUseCase
import com.example.tinkoff_chat_app.domain.usecases.messages.LoadMessagesByTopicUseCase
import com.example.tinkoff_chat_app.domain.usecases.messages.SendMessageUseCase
import com.example.tinkoff_chat_app.models.MessageReaction
import com.example.tinkoff_chat_app.screens.message.MessagesIntents.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessagesViewModel @Inject constructor(
    private val loadMessagesByTopicUseCase: LoadMessagesByTopicUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val changeReactionSelectedStateUseCase: ChangeReactionSelectedStateUseCase
) : ViewModel() {

    val messagesChannel = Channel<MessagesIntents>()
    private val _screenState: MutableStateFlow<MessagesScreenState> =
        MutableStateFlow(MessagesScreenState.Init)
    val screenState get() = _screenState.asStateFlow()

    init {
        subscribeForIntents()
    }

    private fun subscribeForIntents() {
        viewModelScope.launch {
            messagesChannel.consumeAsFlow().collect {
                when (it) {
                    is InitMessagesIntent -> showAllMessages(it.streamName, it.topicName)
                    is UpdateMessagesIntent -> updateMessages(it.streamName, it.topicName)
                    is SendMessageIntent -> sendMessage(
                        content = it.content,
                        streamId = it.streamId,
                        topicName = it.topicName
                    )
                    is ChangeReactionStateIntent -> changeReactionState(
                        reaction = it.reaction,
                        msgId = it.msgId
                    )
                }
            }
        }
    }

    private suspend fun showAllMessages(streamName: String, topicName: String) {
        _screenState.emit(MessagesScreenState.Loading)
        val state = try {
            MessagesScreenState.Success(
                loadMessagesByTopicUseCase(
                    streamName = streamName,
                    topicName = topicName
                )
            )
        } catch (e: Exception) {
            MessagesScreenState.Error(e)
        }
        _screenState.emit(state)
    }

    private suspend fun changeReactionState(reaction: MessageReaction, msgId: String) {
        val state = try {
            changeReactionSelectedStateUseCase(reaction, msgId)
            MessagesScreenState.Init
        } catch (e: Exception) {
            MessagesScreenState.Error(e)
        }
        _screenState.emit(state)
    }

    private suspend fun sendMessage(content: String, topicName: String, streamId: String) {
        val state = try {
            sendMessageUseCase(
                content = content,
                topicName = topicName,
                streamId = streamId
            )
            MessagesScreenState.Init
        } catch (e: Exception) {
            MessagesScreenState.Error(e)
        }
        _screenState.emit(state)
    }

    private suspend fun updateMessages(streamName: String, topicName: String) {

        showAllMessages(streamName, topicName)
    }

}