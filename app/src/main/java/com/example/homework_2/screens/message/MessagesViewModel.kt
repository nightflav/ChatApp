package com.example.homework_2.screens.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework_2.models.MessageReaction
import com.example.homework_2.repository.messages_repository.MessageRepositoryImpl
import com.example.homework_2.repository.messages_repository.MessagesRepository
import com.example.homework_2.screens.message.MessagesIntents.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessagesViewModel @Inject constructor(
    private val repo: MessagesRepository,
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
                    is SendMessageIntent -> sendMessage(content = it.content)
                    is ChangeReactionStateIntent -> changeReactionState(
                        reaction = it.reaction,
                        msgId = it.msgId
                    )
                }
            }
        }
    }

    private suspend fun showAllMessages(streamName: String, topicName: String) {
        repo.getMessagesByTopic(
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
            topicName = "topicName",
            content = content,
            streamId = "streamId"
        ).collect {
            _screenState.emit(it)
        }
    }

    private suspend fun updateMessages(streamName: String, topicName: String) {
        (repo as MessageRepositoryImpl).loadMessagesByTopic(topicName, streamName)
        showAllMessages(streamName, topicName)
    }

}