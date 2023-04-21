package com.example.tinkoff_chat_app.domain.repository.streams_repository

import android.util.Log
import com.example.tinkoff_chat_app.models.stream_screen_models.StreamModel
import com.example.tinkoff_chat_app.network.ChatApi
import com.example.tinkoff_chat_app.utils.toTopicList
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
                topics = null,
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
                topics = null,
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

    suspend fun changeStreamSelectedState(stream: StreamModel, showSubscribed: Boolean) {
        val streamListToChange = if (showSubscribed) subsStreams else streams

        if (streamListToChange?.map { it.id }?.contains(stream.id) == true) {
            Log.d("TAGTAGTAG", "${streamListToChange.indexOf(stream)}")
            streamListToChange[streamListToChange.map { it.name }.indexOf(stream.name)] =
                stream.copy(
                    isSelected = !stream.isSelected,
                    topics = stream.topics ?: chatApi.getTopics(stream.id)
                        .body()?.topics?.toTopicList(stream.id, stream.name)
                )
        } else return
    }
}
