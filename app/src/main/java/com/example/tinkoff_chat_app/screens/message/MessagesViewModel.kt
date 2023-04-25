package com.example.tinkoff_chat_app.screens.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepository
import com.example.tinkoff_chat_app.domain.repository.messages_repository.MessagesRepositoryImpl
import com.example.tinkoff_chat_app.domain.usecases.messages.*
import com.example.tinkoff_chat_app.models.MessageModel
import com.example.tinkoff_chat_app.models.MessageReaction
import com.example.tinkoff_chat_app.screens.message.MessagesIntents.*
import com.example.tinkoff_chat_app.utils.Emojis
import com.example.tinkoff_chat_app.utils.Emojis.getEmojiByName
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessagesViewModel @Inject constructor(
    private val loadMessagesByTopicUseCase: LoadMessagesByTopicUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val changeReactionSelectedStateUseCase: ChangeReactionSelectedStateUseCase,
    private val initMessagesUseCase: InitMessagesUseCase,
    private val saveMessagesUseCase: SaveMessagesUseCase,
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
                        is InitMessagesIntent -> initMessages(
                            streamName = it.streamName,
                            topicName = it.topicName,
                            streamId = it.streamId
                        )
                        is UpdateMessagesIntent -> updateMessages()
                        is SendMessageIntent -> sendMessage(
                            content = it.content,
                        )
                        is ChangeReactionStateIntent -> changeReactionState(
                            reaction = it.reaction,
                            msgId = it.msgId
                        )
                        is LoadMessagesIntent -> loadMoreMessages(
                            amount = it.amount,
                            lastMessageId = it.lastMsgId
                        )
                        is SaveMessagesIntent -> saveMessagesUseCase(
                            maxMessagesToSave = 50,
                            streamName = streamName,
                            topicName = topicName
                        )
                    }
                }
        }
    }

    private suspend fun initMessages(
        streamName: String,
        topicName: String,
        streamId: String
    ) {
        _screenState.emit(
            currState.copy(
                topic = topicName,
                stream = streamName,
                streamId = streamId
            )
        )

        try {
            _screenState.emit(
                currState.copy(
                    messages = initMessagesUseCase(streamName, topicName)?.transformTextToEmojis(),
                    isLoading = false,
                )
            )
        } catch (e: Exception) {
            _screenState.emit(
                currState.copy(
                    isLoading = true
                )
            )
        }
    }

    private suspend fun loadMoreMessages(
        amount: Int,
        lastMessageId: Int?
    ) {
        _screenState.emit(
            currState.copy(
                isNewMessagesLoading = true
            )
        )
        loadMessages(streamName, topicName, amount, lastMessageId)
    }

    private suspend fun loadMessages(
        streamName: String,
        topicName: String,
        amount: Int,
        lastMessageId: Int?
    ) {
        val state = try {
            val loadedMessages =
                loadMessagesByTopicUseCase(
                    streamName = streamName,
                    topicName = topicName,
                    amount = amount,
                    lastMsgId = lastMessageId
                )
            val allMessagesLoaded = loadedMessages == currState.messages
            currState.copy(
                messages = loadedMessages.transformTextToEmojis(),
                isLoading = false,
                isNewMessagesLoading = false,
                allMessagesLoaded = allMessagesLoaded
            )
        } catch (e: Exception) {
            if (currState.messages == null) {
                currState.copy(
                    isLoading = false,
                    error = e
                )
            } else currState
        }
        _screenState.emit(state)
        saveMessagesUseCase(streamName, topicName, 50)
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
        loadMessages(streamName, streamId, 1000, null)
    }

    private fun List<MessageModel>.transformTextToEmojis() = this.map {
        it.copy(
            msg = it.msg.replaceWithEmojis()
        )
    }

    private fun String.replaceWithEmojis(): String {
        val regex = Regex(pattern = ":[A-Za-z_]+:")
        val newMessage = this.replace(regex) {
            val emojiName = it.value.removePrefix(":").removeSuffix(":")
            if (emojiName in Emojis.getEmojisName()) {
                getEmojiByName(emojiName)
            } else {
                it.value
            }
        }
        return newMessage
    }
}


