package com.example.homework_2.repository.streamsRepository

import com.example.homework_2.models.streamScreenModels.StreamModel
import com.example.homework_2.models.streamScreenModels.StreamScreenItem
import com.example.homework_2.network.RetrofitInstance
import com.example.homework_2.screens.stream.StreamScreenState
import com.example.homework_2.utils.toTopicList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StreamsRepositoryImpl : StreamsRepository {

    companion object {
        private var streams: MutableList<StreamModel>? = null
        private var subsStreams: MutableList<StreamModel>? = null
    }

    override suspend fun getAllStreams(): Flow<StreamScreenState> = flow {
        emit(StreamScreenState.Loading)
        if (streams == null) {
            try {
                loadStreams()
                emit(StreamScreenState.Success(streams!!.toListToShow(), false))
            } catch (e: Exception) {
                emit(StreamScreenState.Error)
            }
        } else {
            emit(StreamScreenState.Success(streams!!.toListToShow(), false))
        }
    }

    private suspend fun loadStreams() {
        val resultStreams = mutableListOf<StreamModel>()
        val streamsNetwork = RetrofitInstance.chatApi.getStreams().body()?.streams ?: emptyList()
        for (stream in streamsNetwork) {
            val currStream = StreamModel(
                name = stream.name,
                isSelected = false,
                topics = RetrofitInstance.chatApi.getTopics(stream.streamId.toString())
                    .body()?.topics?.toTopicList(stream.streamId.toString(), stream.name)
                    ?: emptyList(),
                id = stream.streamId.toString()
            )
            resultStreams.add(currStream)
        }
        streams = resultStreams
    }

    override suspend fun getSubscribedStreams(): Flow<StreamScreenState> = flow {
        emit(StreamScreenState.Loading)
        if (subsStreams == null) {
            try {
                loadSubscriptions()
                emit(StreamScreenState.Success(subsStreams!!.toListToShow(), true))
            } catch (e: Exception) {
                emit(StreamScreenState.Error)
            }
        } else {
            emit(StreamScreenState.Success(subsStreams!!.toListToShow(), true))
        }
    }

    private suspend fun loadSubscriptions() {
        val resultStreams = mutableListOf<StreamModel>()
        val streamsNetwork =
            RetrofitInstance.chatApi.getSubscriptions().body()?.subscriptions ?: emptyList()
        for (stream in streamsNetwork) {
            val currStream = StreamModel(
                name = stream.name,
                isSelected = false,
                topics = RetrofitInstance.chatApi.getTopics(stream.streamId.toString())
                    .body()!!.topics.toTopicList(stream.streamId.toString(), stream.name),
                id = stream.streamId.toString()
            )
            resultStreams.add(currStream)
        }
        subsStreams = resultStreams
    }

    override suspend fun getSearchStream(
        request: String,
        subOnly: Boolean
    ): Flow<StreamScreenState> = flow {
        emit(StreamScreenState.Loading)
        if (subOnly) {
            if (subsStreams == null)
                emit(StreamScreenState.Error)
            else
                emit(
                    StreamScreenState.Success(
                        subsStreams!!.filter {
                            it.name.lowercase().contains(request.lowercase())
                        }.toListToShow(),
                        true
                    )
                )
        } else {
            if (streams == null)
                emit(StreamScreenState.Error)
            else
                emit(
                    StreamScreenState.Success(
                        streams!!.filter {
                            it.name.lowercase().contains(request.lowercase())
                        }.toListToShow(),
                        false
                    )
                )
        }
    }

    fun changeStreamSelectedState(stream: StreamModel, showSubscribed: Boolean) {
        val streamListToChange = if (showSubscribed) subsStreams else streams
        if (streamListToChange?.map { it.id }?.contains(stream.id) == true) {
            streamListToChange[streamListToChange.indexOf(stream)] = stream.copy(
                isSelected = !stream.isSelected
            )
        } else return
    }

    fun setTopicMsgCount(topicName: String, count: Int, streamName: String) {
        val topic =
            streams?.firstOrNull { it.name == streamName }?.topics?.firstOrNull { it.name == topicName }
        val topicSubs =
            subsStreams?.firstOrNull { it.name == streamName }?.topics?.firstOrNull { it.name == topicName }
        topic?.msgCount = count
        topicSubs?.msgCount = count
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