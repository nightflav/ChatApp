package com.example.tinkoff_chat_app.screens.stream

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tinkoff_chat_app.domain.usecases.streams.ChangeStreamSelectedStateUseCase
import com.example.tinkoff_chat_app.domain.usecases.streams.LoadAllStreamsUseCase
import com.example.tinkoff_chat_app.domain.usecases.streams.LoadSubStreamsUseCase
import com.example.tinkoff_chat_app.models.stream_screen_models.StreamModel
import com.example.tinkoff_chat_app.models.stream_screen_models.StreamScreenItem
import com.example.tinkoff_chat_app.models.stream_screen_models.TopicModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StreamViewModel @Inject constructor(
    private val loadAllStreamsUseCase: LoadAllStreamsUseCase,
    private val loadSubStreamsUseCase: LoadSubStreamsUseCase,
    private val changeStreamSelectedStateUseCase: ChangeStreamSelectedStateUseCase
) : ViewModel() {

    val streamChannel = Channel<StreamIntents>()
    private val _screenState: MutableStateFlow<ScreenUiState> = MutableStateFlow(ScreenUiState())
    val screenState get() = _screenState.asStateFlow()
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
                when (it) {
                    is StreamIntents.SearchForStreamIntent -> {
                        _screenState.emit(
                            currState.copy(
                                isStreamsLoading = true
                            )
                        )
                        searchForStream(it.request)
                    }
                    is StreamIntents.ShowCurrentStreamsIntent -> {
                        _screenState.emit(
                            currState.copy(
                                isStreamsLoading = true
                            )
                        )
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
        if (!stream.isSelected && stream.topics == null || stream.isSelected)
            viewModelScope.launch {
                _screenState.emit(
                    currState.copy(
                        streams = currState.streams?.applySearchFilter(currState.request)
                            ?.toListToShow()
                            .addLoadingTopicsShimmer(stream)
                    )
                )
            }

        changeStreamSelectedStateUseCase(stream, currState.showSubs)
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
            val loadedStreams = loadAllStreamsUseCase()
            _screenState.emit(
                currState.copy(
                    isStreamsLoading = false,
                    streams = loadedStreams.applySearchFilter(currState.request).toListToShow()
                )
            )
        } catch (e: Throwable) {
            _screenState.emit(
                currState.copy(
                    isStreamsLoading = false,
                    error = e
                )
            )
        }
    }

    private suspend fun loadSubscribedStream() {
        try {
            val loadedStreams = loadSubStreamsUseCase()
            _screenState.emit(
                currState.copy(
                    isStreamsLoading = false,
                    streams = loadedStreams.applySearchFilter(currState.request).toListToShow()
                )
            )
        } catch (e: Throwable) {
            _screenState.emit(
                currState.copy(
                    isStreamsLoading = false,
                    error = e
                )
            )
        }
    }

    private suspend fun searchForStream(request: String) {
        try {
            val streamsToSearchFrom = if (currState.showSubs)
                loadSubStreamsUseCase()
            else
                loadAllStreamsUseCase()

            _screenState.emit(
                currState.copy(
                    request = request,
                    streams = streamsToSearchFrom.applySearchFilter(request).toListToShow(),
                    isStreamsLoading = false,
                )
            )
        } catch (e: Throwable) {
            _screenState.emit(
                currState.copy(
                    request = request,
                    isStreamsLoading = false,
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
                stream.topics?.let {
                    result.addAll(
                        it
                    )
                }
        }
        return result
    }

    private fun List<StreamScreenItem>?.addLoadingTopicsShimmer(stream: StreamModel): List<StreamScreenItem> {
        if (this == null) return emptyList()

        val result = mutableListOf<StreamScreenItem>()
        for (item in this) {

            result.add(item)
            if (item is StreamModel && item.name == stream.name && !stream.isSelected) {
                repeat(1) {
                    result.add(
                        TopicModel(
                            id = "shimmer",
                            parentId = item.id,
                            parentName = item.name,
                            isLoading = true,
                        )
                    )
                }
            }
        }
        return result
    }
}
