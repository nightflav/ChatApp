package com.example.homework_2.repository.streams_repository

import com.example.homework_2.models.stream_screen_models.StreamModel
import com.example.homework_2.network.ChatApi
import com.example.homework_2.utils.toTopicList
import javax.inject.Inject

class StreamsRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi
) : StreamsRepository {

    private var streams: MutableList<StreamModel>? = null
    private var subsStreams: MutableList<StreamModel>? = null

    override suspend fun getAllStreams() = if (streams == null) {
        loadStreams()
        streams!!
    } else {
        streams!!
    }

    private suspend fun loadStreams() {
        val resultStreams = mutableListOf<StreamModel>()
        val streamsNetwork = chatApi.getStreams().body()?.streams ?: emptyList()
        for (stream in streamsNetwork) {
            val currStream = StreamModel(
                name = stream.name,
                isSelected = false,
                topics = chatApi.getTopics(stream.streamId.toString())
                    .body()?.topics?.toTopicList(stream.streamId.toString(), stream.name)
                    ?: emptyList(),
                id = stream.streamId.toString()
            )
            resultStreams.add(currStream)
        }
        streams = resultStreams
    }

    override suspend fun getSubscribedStreams() = if (subsStreams == null) {
        loadSubscriptions()
        subsStreams!!
    } else {
        subsStreams!!
    }

    private suspend fun loadSubscriptions() {
        val resultStreams = mutableListOf<StreamModel>()
        val streamsNetwork =
            chatApi.getSubscriptions().body()?.subscriptions ?: emptyList()
        for (stream in streamsNetwork) {
            val currStream = StreamModel(
                name = stream.name,
                isSelected = false,
                topics = chatApi.getTopics(stream.streamId.toString())
                    .body()!!.topics.toTopicList(stream.streamId.toString(), stream.name),
                id = stream.streamId.toString()
            )
            resultStreams.add(currStream)
        }
        subsStreams = resultStreams
    }

    fun setTopicMsgCount(topicName: String, count: Int, streamName: String) {
        val topic =
            streams?.firstOrNull { it.name == streamName }?.topics?.firstOrNull { it.name == topicName }
        val topicSubs =
            subsStreams?.firstOrNull { it.name == streamName }?.topics?.firstOrNull { it.name == topicName }
        topic?.msgCount = count
        topicSubs?.msgCount = count
    }

    fun changeStreamSelectedState(stream: StreamModel, showSubscribed: Boolean) {
        val streamListToChange = if (showSubscribed) subsStreams else streams
        if (streamListToChange?.map { it.id }?.contains(stream.id) == true) {
            streamListToChange[streamListToChange.indexOf(stream)] = stream.copy(
                isSelected = !stream.isSelected
            )
        } else return
    }
}
