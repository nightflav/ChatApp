package com.example.homework_2.screens.stream

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework_2.datasource.StreamDatasource
import com.example.homework_2.models.Stream
import com.example.homework_2.models.Topic
import com.example.homework_2.runCatchingNonCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StreamViewModel : ViewModel() {

    private val _searchState: MutableStateFlow<StreamScreenState> =
        MutableStateFlow(StreamScreenState.Init)
    val searchState get() = _searchState.asStateFlow()
    val searchRequestState: MutableSharedFlow<String> = MutableSharedFlow()
    val openTopicState: MutableSharedFlow<Stream> = MutableSharedFlow()
    val subscribedListState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var listOfStreams: MutableList<Stream> = mutableListOf()
    private var listToShow: MutableList<Any> = mutableListOf()
    private val listState: MutableSharedFlow<MutableList<Any>?> = MutableSharedFlow()

    init {
        subscribeToSearchRequest()
        subscribeToOpenTopic()
        subscribeToChangeToSubscribedList()
        subscribeToListChanges()
        viewModelScope.launch {
            listOfStreams = StreamDatasource.getAllStreams()
            listToShow = listOfStreams.toMutableList()
            listState.emit(getSubscribedItems())
        }
    }

    private suspend fun searchForStream(request: String) {
        clearFromTopics()
        val searchResult = runCatchingNonCancellation {
            _searchState.emit(StreamScreenState.Loading)
            if (request.isEmpty() || request.isBlank()) {
                listOfStreams = StreamDatasource.getAllStreams()
                getSubscribedItems()
            } else {
                listOfStreams = StreamDatasource.getSearchStreams(request)
                getSubscribedItems()
            }
        }.onFailure {
            Log.d("Channels", "searchForStreamError")
        }.getOrNull()

        listState.emit(searchResult)
    }

    private suspend fun updateTopicsOfCurrentStream(stream: Stream) {

        changeStreamSelectedState(stream)
        val currStream = listOfStreams[listOfStreams.indexOf(stream)]
        Log.d("012345", "currStream is $currStream")
        val currList: MutableList<Any> = getSubscribedItems().toMutableList()
        Log.d("012345", "currList is $currList")
        val topicsToAdd = currStream.topics
        if (!currStream.isSelected) {
            Log.d("55555", "true $currStream")
            listToShow =
                currList.filter { it is Stream || it is Topic && it.parentId != currStream.id }
                    .toMutableList()
            listState.emit(listToShow)
        } else {
            Log.d("55555", "false $currStream")
            val indexOfStream = currList.indexOf(currStream)
            currList.addAll(indexOfStream + 1, topicsToAdd)
            listToShow = currList
            listState.emit(listToShow)
        }
        Log.d("012345", "sent to listState $listOfStreams")

    }

    private fun getSubscribedItems(): MutableList<Any> {
        var currList = listOfStreams
        currList = if (subscribedListState.value)
            currList.filter { it.isSubscribed }.toMutableList()
        else
            currList.toMutableList()
        return currList.toMutableList()
    }

    private suspend fun showUpdatedList() {
        val currList: MutableList<Any> = getSubscribedItems()
        Log.d("11111", "$currList")
        listState.emit(currList)
    }

    private fun getScreenState(state: MutableList<Any>?): StreamScreenState {
        Log.d("012345", "return to fragment -> $state")
        return state?.let { StreamScreenState.Data(it.toList()) }
            ?: StreamScreenState.Error
    }

    private fun subscribeToListChanges() {
        listState
            .onEach { _searchState.emit(getScreenState(it)) }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun subscribeToSearchRequest() {
        searchRequestState
            .distinctUntilChanged()
            .debounce(500L)
            .flatMapLatest {
                flow {
                    emit(
                        searchForStream(it)
                    )
                }
            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun subscribeToOpenTopic() {
        openTopicState
            .debounce(50L)
            .flatMapLatest {
                flow {
                    emit(
                        updateTopicsOfCurrentStream(
                            it
                        )
                    )
                }
            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    private fun subscribeToChangeToSubscribedList() {
        subscribedListState
            .onEach { clearFromTopics(); showUpdatedList() }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    private fun changeStreamSelectedState(stream: Stream) {
        if (stream.id in listOfStreams.map { it.id }) {
            listOfStreams[listOfStreams.indexOf(stream)].isSelected = !stream.isSelected
            Log.d("012345", "change to ${listOfStreams[listOfStreams.indexOf(stream)].isSelected}")
        }
        else return
    }

    private fun clearFromTopics() {
        for (item in listOfStreams)
            item.isSelected = false
        listToShow = listOfStreams.toMutableList()
    }
}
