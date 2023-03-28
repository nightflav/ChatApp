package com.example.homework_2.screens.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.homework_2.datasource.MessageDatasource.addMessage
import com.example.homework_2.datasource.MessageDatasource.getMessages
import com.example.homework_2.models.SingleMessage
import com.example.homework_2.runCatchingNonCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MessagesViewModel(private val topicId: String) : ViewModel() {

    class Factory (
        private val topicId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MessagesViewModel(topicId) as T
        }
    }

    private val _screenState: MutableStateFlow<MessageScreenState> =
        MutableStateFlow(MessageScreenState.Init)
    val screenState get() = _screenState.asStateFlow()
    var messages: List<SingleMessage> = emptyList()
    val newSentMessage: MutableSharedFlow<SingleMessage> = MutableSharedFlow()

    init {
        subscribeToSendMessage()
        viewModelScope.launch {
            messages = getMessages(topicId)
            _screenState.emit(showMessages())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun subscribeToSendMessage() {
        newSentMessage
            .onEach {
                addMessage(topicId, it)
                messages = getMessages(topicId)
            }
            .flatMapLatest { flow { emit(showMessages()) } }
            .onEach { _screenState.emit(it) }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    private suspend fun showMessages(): MessageScreenState {
        val result = runCatchingNonCancellation {
            _screenState.emit(MessageScreenState.Loading)
            getMessages(topicId)
        }.getOrNull()

        return result?.let { MessageScreenState.Data(it) }
            ?: MessageScreenState.Error
    }

}