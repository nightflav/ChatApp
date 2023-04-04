package com.example.homework_2.screens.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.homework_2.datasource.MessageDatasource.addMessage
import com.example.homework_2.datasource.MessageDatasource.getMessagesByTopic
import com.example.homework_2.datasource.MessageDatasource.loadMessagesByTopic
import com.example.homework_2.datasource.MessageDatasource.removeReaction
import com.example.homework_2.datasource.MessageDatasource.sendReaction
import com.example.homework_2.models.MessageReaction
import com.example.homework_2.models.SingleMessage
import com.example.homework_2.runCatchingNonCancellation
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MessagesViewModel(
    private val topicName: String,
    private val streamName: String,
    private val streamId: String
) : ViewModel() {

    class Factory (
        private val topicName: String,
        private val streamName: String,
        private val streamId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MessagesViewModel(topicName, streamName, streamId) as T
        }
    }

    private val _screenState: MutableStateFlow<MessageScreenState> =
        MutableStateFlow(MessageScreenState.Init)
    val screenState get() = _screenState.asStateFlow()
    private var messages: List<SingleMessage>? = null

    init {
        viewModelScope.launch {
            _screenState.emit(MessageScreenState.Loading)
            _screenState.emit(showMessages())
        }
    }

    private suspend fun showMessages(): MessageScreenState {
        val result = runCatchingNonCancellation {
            _screenState.emit(MessageScreenState.Loading)
            getMessagesByTopic(topicName, streamName)
        }.getOrNull()

        messages = result

        return result?.let { MessageScreenState.Data(it) }
            ?: MessageScreenState.Error
    }

    fun setReactionOnMessage(msgId: String, reaction: MessageReaction) {
        val reactionName = reaction.reaction.name
        viewModelScope.launch {
            if(reaction.isSelected)
                removeReaction(msgId, reactionName)
            else
                sendReaction(msgId, reactionName)
        }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            addMessage(
                streamId = streamId,
                topicName = topicName,
                msg = content
            )
        }
    }

    fun updateMessages() {
        viewModelScope.launch {
            _screenState.emit(MessageScreenState.Loading)
            loadMessagesByTopic(topicName, streamName)
            _screenState.emit(showMessages())
        }
    }

}