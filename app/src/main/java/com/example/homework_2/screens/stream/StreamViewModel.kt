package com.example.homework_2.screens.stream

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework_2.datasource.StreamDatasource.getAllStreams
import com.example.homework_2.datasource.StreamDatasource.getSearchStreams
import com.example.homework_2.datasource.StreamDatasource.getSubscribedStreams
import com.example.homework_2.models.Stream
import com.example.homework_2.runCatchingNonCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StreamViewModel : ViewModel() {

    private val _screenState: MutableStateFlow<StreamScreenState> =
        MutableStateFlow(StreamScreenState.Init)
    val screenState get() = _screenState.asStateFlow()

    private var listOfStreams: List<Stream>? = emptyList()
    private var listOfSubscriptions: List<Stream>? = emptyList()
    private val listToShow: List<Any>?
        get() = toListToShow()

    private fun toListToShow(): List<Any>? {
        val listFrom = (if (showSubscribed) listOfSubscriptions else listOfStreams) ?: return null
        val result = mutableListOf<Any>()
        for (stream in listFrom) {
            result.add(stream)
            if (stream.isSelected)
                result.addAll(
                    stream.topics
                )
        }
        return result
    }

    private val listState: MutableSharedFlow<List<Any>?> = MutableSharedFlow()

    private val searchRequestState: MutableSharedFlow<String> = MutableSharedFlow()
    private val streamState: MutableSharedFlow<Stream> = MutableSharedFlow()

    var showSubscribed = true

    init {
        subscribeToListStateChanges()
        subscribeToSearch()
        viewModelScope.launch {
            _screenState.emit(loadStreams())
        }
    }

    private suspend fun loadStreams(): StreamScreenState {
        val loadedStreams = runCatchingNonCancellation {
            _screenState.emit(StreamScreenState.Loading)
            listOfStreams = getAllStreams()
            listOfSubscriptions = getSubscribedStreams()
            listToShow
        }.onFailure {
            Log.d(
                "StreamViewModelDebugLog",
                "Failed on searching for stream, caused by ${it.cause}"
            )
        }.getOrNull()

        return getScreenState(loadedStreams)
    }

    private fun getScreenState(state: List<Any>?): StreamScreenState {
        return state?.let { StreamScreenState.Data(it.toList()) }
            ?: StreamScreenState.Error
    }

    private fun subscribeToListStateChanges() {
        listState
            .onEach {
                _screenState.emit(getScreenState(it))
            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun subscribeToSearch() {
        searchRequestState
            .distinctUntilChanged()
            .debounce(500L)
            .flatMapLatest {
                flow {
                    emit(
                        searchForStreamNetwork(it)
                    )
                }
            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    private suspend fun searchForStreamNetwork(request: String) {
        val searchResult = runCatchingNonCancellation {
            _screenState.emit(StreamScreenState.Loading)
            if (request.isEmpty() || request.isBlank())
                if (showSubscribed) {
                    getSubscribedStreams()

                } else {
                    getAllStreams()
                }
            else {
                listOfStreams = getSearchStreams(request, showSubscribed)
                listOfStreams
            }
        }.onFailure {
            Log.d(
                "StreamViewModelDebugLog",
                "Failed on searching for stream, caused by ${it.cause}"
            )
        }.getOrNull()

        if (showSubscribed)
            listOfSubscriptions = searchResult
        else
            listOfStreams = searchResult

        refresh()
    }

    fun searchForStream(request: String) {
        viewModelScope.launch {
            searchRequestState.emit(request)
        }
    }

    private fun changeStreamIsSelected(streamToChange: Stream) {
        val currList = (if (showSubscribed) listOfSubscriptions else listOfStreams)!!
        if (streamToChange in currList)
            currList[currList.indexOf(streamToChange)].isSelected =
                !currList[currList.indexOf(streamToChange)].isSelected
        refresh()
    }

    fun renewTopics(stream: Stream) {
        changeStreamIsSelected(stream)
        viewModelScope.launch {
            streamState.emit(stream)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _screenState.emit(getScreenState(listToShow))
        }
    }
}
