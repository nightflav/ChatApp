package com.example.tinkoff_chat_app.screens.stream

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tinkoff_chat_app.domain.repository.streams_repository.StreamsRepository
import com.example.tinkoff_chat_app.domain.repository.topics_repository.TopicsRepository
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.StreamModel
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.StreamScreenItem
import com.example.tinkoff_chat_app.models.ui_models.stream_screen_models.TopicModel
import com.example.tinkoff_chat_app.utils.Resource
import com.example.tinkoff_chat_app.utils.StreamMappers.toStreamModelList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class StreamViewModel @Inject constructor(
    private val streamsRepo: StreamsRepository,
    private val topicsRepo: TopicsRepository
) : ViewModel() {

    val streamChannel = Channel<StreamIntents>()
    private val _screenState: MutableStateFlow<StreamScreenUiState> =
        MutableStateFlow(StreamScreenUiState())
    val screenState get() = _screenState.asStateFlow()
    private val currState
        get() = screenState.value
    private var shouldFetch = true
    private var shouldFetchSubs = true

    init {
        subscribeToIntents()
//        Need further implementation
//        viewModelScope.launch {
//            subscribeForUpdates()
//        }
        viewModelScope.launch {
            streamChannel.send(
                StreamIntents.InitStreamsIntent
            )
        }
    }

//    private suspend fun subscribeForUpdates() {
//        try {
//            val queue =
//                streamsRepo.registerQueue("subscription").toMutableMap()
//            while (true) {
//                try {
//                    val newEventId = streamsRepo.getEventsFromQueue(
//                        queue = queue
//                    )
//                    queue[RealTimeEvents.LAST_EVENT_ID_KEY] = newEventId
//                } catch (_: Exception) {
//                }
//                delay(500L)
//            }
//        } catch (_: Exception) {
//        }
//    }

    @OptIn(FlowPreview::class)
    private fun subscribeToIntents() {
        viewModelScope.launch {
            streamChannel.consumeAsFlow()
                .debounce(100L)
                .collect {
                    when (it) {
                        StreamIntents.InitStreamsIntent -> {
                            subscribeForStreams()
                            streamsRepo.loadAllSubscriptions(true) {}
                        }
                        is StreamIntents.SearchForStreamIntent -> {
                            _screenState.emit(
                                currState.copy(
                                    request = it.request
                                )
                            )
                            updateUi(onError = it.onError)
                        }
                        is StreamIntents.ShowCurrentStreamTopicsIntent -> {
                            changeStreamSelectedState(it.stream, it.onError)
                        }
                        is StreamIntents.ShowCurrentStreamsIntent -> {
                            _screenState.emit(
                                currState.copy(
                                    showSubs = it.showSubscribed
                                )
                            )
                            updateUi(onError = it.onError)
                        }
                        is StreamIntents.CreateNewStreamIntent -> {
                            streamsRepo.subscribeForStream(
                                it.name,
                                it.description,
                                it.announce
                            )
                            if (currState.showSubs)
                                streamsRepo.loadAllSubscriptions(true) {
                                    it.onError()
                                }
                            else
                                streamsRepo.loadAllSubscriptions(true) {
                                    it.onError()
                                }
                        }
                    }
                }
        }
    }

    private suspend fun updateUi(onError: () -> Unit) {
        if (currState.showSubs) {
            streamsRepo.loadAllSubscriptions(shouldFetchSubs) { onError() }
            shouldFetchSubs = false
        } else {
            streamsRepo.loadAllStreams(shouldFetch) { onError() }
            shouldFetch = false
        }
    }

    private suspend fun subscribeForStreams() {
        viewModelScope.launch {
            streamsRepo.currStreams.collect {
                when (it) {
                    is Resource.Success -> {
                        if (currState.streams != it.data && it.data!!.isNotEmpty())
                            _screenState.emit(
                                currState.copy(
                                    streams = it.data.toStreamModelList()
                                        .applySearchFilter(currState.request)
                                        .toListToShow(),
                                    isStreamsLoading = false
                                )
                            )
                    }
                    is Resource.Error -> {
                        _screenState.emit(
                            currState.copy(
                                error = it.error,
                                isStreamsLoading = false
                            )
                        )
                    }
                    is Resource.Loading -> {
                        if (currState.streams.isNullOrEmpty())
                            _screenState.emit(
                                currState.copy(
                                    isStreamsLoading = true
                                )
                            )
                    }
                }
            }
        }
    }

    private fun List<StreamModel>.toListToShow(): List<StreamScreenItem> {
        val result = mutableListOf<StreamScreenItem>()
        for (stream in this.sortedBy { it.id }) {
            result.add(stream)
            if (stream.isSelected && !stream.topics.isNullOrEmpty())
                result.addAll(stream.topics)
        }
        return result
    }

    private suspend fun changeStreamSelectedState(stream: StreamModel, onError: () -> Unit) {
        _screenState.emit(
            currState.copy(
                streams = currState.streams
                    .addLoadingTopicsShimmer(stream)
            )
        )
        val loadedTopics = topicsRepo.loadAllTopicsOfCurrentStream(
            streamId = stream.id,
            streamName = stream.name,
            shouldFetch = !stream.isSelected
        )
        when (loadedTopics) {
            is Resource.Success -> {
                streamsRepo.updateStreamTopics(streamId = stream.id, newTopics = loadedTopics.data)
            }
            else -> {
                onError()
                _screenState.emit(
                    currState.copy(
                        streams = currState.streams
                            .removeAllShimmers()
                    )
                )
            }
        }
    }

    private fun List<StreamModel>.applySearchFilter(request: String?): List<StreamModel> {
        val result = mutableListOf<StreamModel>()
        if (request.isNullOrBlank() || request.isEmpty()) return this
        for (item in this) {
            if (item.name.contains(request, true)) {
                result.add(item)
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
                repeat(3) {
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

    private fun List<StreamScreenItem>?.removeAllShimmers(): List<StreamScreenItem>? =
        this?.filter {
            (it is TopicModel && it.id != "shimmer") || it is StreamModel
        }
}


