package com.example.homework_2.screens.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.homework_2.models.MessageReaction
import com.example.homework_2.repository.messages_repository.MessageRepositoryImpl
import com.example.homework_2.screens.message.MessagesIntents.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MessagesViewModel(
    private val topicName: String,
    private val streamName: String,
    private val streamId: String
) : ViewModel() {

    class Factory(
        private val topicName: String,
        private val streamName: String,
        private val streamId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MessagesViewModel(topicName, streamName, streamId) as T
        }
    }

    val messagesChannel = Channel<MessagesIntents>()
    private val _screenState: MutableStateFlow<MessagesScreenState> =
        MutableStateFlow(MessagesScreenState.Init)
    val screenState get() = _screenState.asStateFlow()
    private val repo = MessageRepositoryImpl()


    init {
        subscribeForIntents()
    }

    private fun subscribeForIntents() {
        viewModelScope.launch {
            messagesChannel.consumeAsFlow().collect {
                when (it) {
                    is InitMessagesIntent -> showAllMessages()
                    is UpdateMessagesIntent -> updateMessages()
                    is SendMessageIntent -> sendMessage(content = it.content)
                    is ChangeReactionStateIntent -> changeReactionState(
                        reaction = it.reaction,
                        msgId = it.msgId
                    )
                }
            }
        }
    }

    private suspend fun showAllMessages() {
        MessageRepositoryImpl().getMessagesByTopic(
            topicName = topicName,
            streamName = streamName
        ).collect {
            _screenState.emit(it)
        }
    }

    private suspend fun changeReactionState(reaction: MessageReaction, msgId: String) {
        val reactionName = reaction.reaction.name
        if (reaction.isSelected)
            repo.removeReaction(msgId, reactionName).collect {
                _screenState.emit(it)
            }
        else
            repo.sendReaction(msgId, reactionName).collect {
                _screenState.emit(it)
            }
    }

    private suspend fun sendMessage(content: String) {
        repo.sendMessage(
            topicName = topicName,
            content = content,
            streamId = streamId
        ).collect {
            _screenState.emit(it)
        }
    }

    private suspend fun updateMessages() {
        repo.loadMessagesByTopic(topicName, streamName)
        showAllMessages()
    }

}