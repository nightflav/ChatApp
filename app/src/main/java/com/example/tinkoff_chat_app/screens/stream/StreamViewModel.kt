package com.example.tinkoff_chat_app.screens.stream

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tinkoff_chat_app.domain.usecases.streams.*
import com.example.tinkoff_chat_app.models.stream_screen_models.StreamModel
import com.example.tinkoff_chat_app.models.stream_screen_models.StreamScreenItem
import com.example.tinkoff_chat_app.models.stream_screen_models.TopicModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class StreamViewModel @Inject constructor(
    private val loadAllStreamsUseCase: LoadAllStreamsUseCase,
    private val loadSubStreamsUseCase: LoadSubStreamsUseCase,
    private val changeStreamSelectedStateUseCase: ChangeStreamSelectedStateUseCase,
    private val loadAllStreamsLocalUseCase: LoadAllStreamsLocalUseCase,
    private val loadSubStreamsLocalUseCase: LoadSubStreamsLocalUseCase
) : ViewModel() {

    val streamChannel = Channel<StreamIntents>()
    private val _screenState: MutableStateFlow<StreamScreenUiState> =
        MutableStateFlow(StreamScreenUiState())
    val screenState get() = _screenState.asStateFlow()
    private val currState
        get() = screenState.value

    init {
        subscribeToIntents()
        viewModelScope.launch {
            streamChannel.send(
                StreamIntents.InitStreamsIntent
            )
            streamChannel.send(
                StreamIntents.ShowCurrentStreamsIntent(true)
            )
        }
    }

    @OptIn(FlowPreview::class)
    private fun subscribeToIntents() {
        viewModelScope.launch {
            streamChannel.consumeAsFlow()
                .debounce(100L)
                .collect {
                when (it) {
                    StreamIntents.InitStreamsIntent -> {
                        initStreams()
                    }
                    is StreamIntents.ShowCurrentStreamsIntent -> {
                        showCurrentStreams(it.showSubscribed)
                    }
                    is StreamIntents.SearchForStreamIntent -> {
                        searchForStream(it.request)
                    }
                    is StreamIntents.UpdateStreamSelectedStateIntent -> {
                        changeStreamSelectedState(it.stream)
                    }
                }
            }
        }
    }

    private suspend fun initStreams() {
        _screenState.emit(
            currState.copy(
                showSubs = true,
                streams = loadSubStreamsLocalUseCase().applySearchFilter(
                    currState.request
                )
                    .toListToShow(),
                isStreamsLoading = false
            )
        )
        if (currState.streams.isNullOrEmpty()) {
            _screenState.emit(
                currState.copy(
                    isStreamsLoading = true
                )
            )
        }
    }

    private suspend fun changeStreamSelectedState(stream: StreamModel) {
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
        if (currState.streams == null) {
            _screenState.emit(
                currState.copy(
                    isStreamsLoading = true
                )
            )
        }

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
                    streams = loadedStreams.applySearchFilter(currState.request).toListToShow(),
                    showSubs = false
                )
            )
        } catch (e: Throwable) {
            _screenState.emit(
                currState.copy(
                    showSubs = false,
                    streams = loadAllStreamsLocalUseCase().applySearchFilter(currState.request)
                        .toListToShow(),
                    isStreamsLoading = false
                )
            )
            if (currState.streams == null) {
                _screenState.emit(
                    currState.copy(
                        isStreamsLoading = false,
                        error = e
                    )
                )
            }
        }
    }

    private suspend fun loadSubscribedStream() {
        try {
            val loadedStreams = loadSubStreamsUseCase()
            _screenState.emit(
                currState.copy(
                    isStreamsLoading = false,
                    streams = loadedStreams.applySearchFilter(currState.request).toListToShow(),
                    showSubs = true
                )
            )
        } catch (e: Throwable) {
            _screenState.emit(
                currState.copy(
                    showSubs = true,
                    streams = loadSubStreamsLocalUseCase().applySearchFilter(
                        currState.request
                    )
                        .toListToShow(),
                    isStreamsLoading = false
                )
            )
            if (currState.streams == null) {
                _screenState.emit(
                    currState.copy(
                        isStreamsLoading = false,
                        error = e
                    )
                )
            }
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
            val streamsToSearchFrom = if (currState.showSubs)
                loadSubStreamsLocalUseCase()
            else
                loadAllStreamsLocalUseCase()

            _screenState.emit(
                currState.copy(
                    request = request,
                    streams = streamsToSearchFrom.applySearchFilter(request).toListToShow(),
                    isStreamsLoading = false,
                )
            )
        } catch (e: Exception) {
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
            this.filterIsInstance<StreamModel>().sortedBy { it.name.lowercase() }
        else
            this.filterIsInstance<StreamModel>().sortedBy { it.name.lowercase() }.filter {
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
