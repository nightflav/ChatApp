package com.example.homework_2.screens.stream

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework_2.models.streamScreenModels.StreamModel
import com.example.homework_2.repository.streamsRepository.StreamsRepositoryImpl
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class StreamViewModel : ViewModel() {

    val streamChannel = Channel<StreamIntents>()
    private val _screenState: MutableStateFlow<StreamScreenState> =
        MutableStateFlow(StreamScreenState.Init)
    val screenState get() = _screenState.asStateFlow()
    private val repo = StreamsRepositoryImpl()

    init {
        viewModelScope.launch {
            loadSubscribedStream()
        }
        subscribeToIntents()
    }

    private fun subscribeToIntents() {
        viewModelScope.launch {
            streamChannel.consumeAsFlow().collect {
                when (it) {
                    is StreamIntents.InitStreams -> {
                        _screenState.emit(screenState.value)
                    }
                    is StreamIntents.SearchForStreamIntent -> {
                        if(it.request.isNotBlank())
                            searchForStream(it.request, it.showSubscribed)
                    }
                    is StreamIntents.ChangeSubscribedStreamsState -> {
                        changeSubsState(it.showSubscribed)
                    }
                    is StreamIntents.UpdateStreamSelectedState -> {
                        changeStreamSelectedState(it.stream, it.showSubscribed)
                    }
                }
            }
        }
    }

    private suspend fun changeStreamSelectedState(stream: StreamModel, showSubscribed: Boolean) {
        repo.changeStreamSelectedState(stream, showSubscribed)
        changeSubsState(showSubscribed)
    }

    private suspend fun changeSubsState(showSubscribed: Boolean) {
        if (showSubscribed)
            loadSubscribedStream()
        else
            loadAllStreams()
    }

    private suspend fun loadAllStreams() {
        repo.getAllStreams().collect {
            _screenState.emit(it)
        }
    }

    private suspend fun searchForStream(request: String, showSubscribed: Boolean) {
        repo.getSearchStream(request, showSubscribed).collect {
            _screenState.emit(it)
        }
    }

    private suspend fun loadSubscribedStream() {
        repo.getSubscribedStreams().collect {
            _screenState.emit(it)
        }
    }
}
