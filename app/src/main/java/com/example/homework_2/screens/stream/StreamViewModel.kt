package com.example.homework_2.screens.stream

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework_2.models.streamScreenModels.StreamModel
import com.example.homework_2.models.streamScreenModels.StreamScreenItem
import com.example.homework_2.repository.streamsRepository.StreamsRepositoryImpl
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class StreamViewModel : ViewModel() {

    val streamChannel = Channel<StreamIntents>()
    private val _screenState: MutableStateFlow<ScreenUiState> = MutableStateFlow(ScreenUiState())
    val screenState get() = _screenState.asStateFlow()
    private val repo = StreamsRepositoryImpl()
    private val currState
        get() = screenState.value

    init {
        subscribeToIntents()
        viewModelScope.launch {
            streamChannel.send(
                StreamIntents.ShowCurrentStreamsIntent(true)
            )
        }
    }

    private fun subscribeToIntents() {
        viewModelScope.launch {

            streamChannel.consumeAsFlow().collect() {
                _screenState.emit(
                    currState.copy(
                        isLoading = true
                    )
                )
                when (it) {
                    is StreamIntents.SearchForStreamIntent -> {
                        searchForStream(it.request)
                    }
                    is StreamIntents.ShowCurrentStreamsIntent -> {
                        showCurrentStreams(it.showSubscribed)
                    }
                    is StreamIntents.UpdateStreamSelectedStateIntent -> {
                        changeStreamSelectedState(it.stream)
                    }
                }
            }
        }
    }

    private suspend fun changeStreamSelectedState(stream: StreamModel) {
        repo.changeStreamSelectedState(stream, currState.showSubs)
        showCurrentStreams(currState.showSubs)
    }

    private suspend fun showCurrentStreams(showSubscribed: Boolean) {
        _screenState.emit(
            currState.copy(
                showSubs = showSubscribed
            )
        )
        if (showSubscribed)
            loadSubscribedStream()
        else
            loadAllStreams()
    }

    private suspend fun loadAllStreams() {
        try {
            val loadedStreams = repo.getAllStreams()
            _screenState.emit(
                currState.copy(
                    isLoading = false,
                    streams = loadedStreams.applySearchFilter(currState.request).toListToShow()
                )
            )
        } catch (e: Throwable) {
            _screenState.emit(
                currState.copy(
                    isLoading = false,
                    error = e
                )
            )
        }
    }

    private suspend fun loadSubscribedStream() {
        try {
            val loadedStreams = repo.getSubscribedStreams()
            _screenState.emit(
                currState.copy(
                    isLoading = false,
                    streams = loadedStreams.applySearchFilter(currState.request).toListToShow()
                )
            )
        } catch (e: Throwable) {
            _screenState.emit(
                currState.copy(
                    isLoading = false,
                    error = e
                )
            )
        }
    }

    private suspend fun searchForStream(request: String) {
        try {
            val streamsToSearchFrom = if (currState.showSubs)
                repo.getSubscribedStreams()
            else
                repo.getAllStreams()

            _screenState.emit(
                currState.copy(
                    request = request,
                    streams = streamsToSearchFrom.applySearchFilter(request).toListToShow(),
                    isLoading = false
                )
            )
        } catch (e: Throwable) {
            _screenState.emit(
                currState.copy(
                    request = request,
                    isLoading = false,
                    error = e
                )
            )
        }
    }

    private fun List<StreamScreenItem>.applySearchFilter(request: String?): List<StreamModel> =
        if (request.isNullOrBlank())
            this.filterIsInstance<StreamModel>()
        else
            this.filterIsInstance<StreamModel>().filter {
                it.name.lowercase().contains(request.lowercase())
            }

    private fun List<StreamModel>.toListToShow(): List<StreamScreenItem> {
        val result = mutableListOf<StreamScreenItem>()
        for (stream in this) {
            result.add(stream)
            if (stream.isSelected)
                result.addAll(
                    stream.topics
                )
        }
        return result
    }
}
