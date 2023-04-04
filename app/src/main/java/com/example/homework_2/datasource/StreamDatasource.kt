package com.example.homework_2.datasource

import com.example.homework_2.models.Stream
import com.example.homework_2.models.Topic
import com.example.homework_2.network.RetrofitInstance.Companion.chatApi


object StreamDatasource {

    private var streams: List<Stream> = emptyList()
    private var subsStreams: List<Stream> = emptyList()

    suspend fun getAllStreams(): List<Stream> {
        return streams.ifEmpty {
            loadStreams()
            streams
        }
    }

    private suspend fun loadStreams() {
        val resultStreams = mutableListOf<Stream>()
        val streamsNetwork = chatApi.getStreams().body()?.streams ?: emptyList()
        for (stream in streamsNetwork) {
            val currStream = Stream(
                name = stream.name,
                isSelected = false,
                topics = chatApi.getTopics(stream.stream_id.toString())
                    .body()?.topics?.toTopicList(stream.stream_id.toString(), stream.name) ?: emptyList(),
                id = stream.stream_id.toString()
            )
            resultStreams.add(currStream)
        }
        streams = resultStreams
    }

    suspend fun getSubscribedStreams(): List<Stream> {
        return subsStreams.ifEmpty {
            loadSubscriptions()
            subsStreams
        }
    }

    private suspend fun loadSubscriptions() {
        val resultStreams = mutableListOf<Stream>()
        val streamsNetwork = chatApi.getSubscriptions().body()?.subscriptions ?: emptyList()
        for (stream in streamsNetwork) {
            val currStream = Stream(
                name = stream.name,
                isSelected = false,
                topics = chatApi.getTopics(stream.stream_id.toString())
                    .body()!!.topics.toTopicList(stream.stream_id.toString(), stream.name),
                id = stream.stream_id.toString()
            )
            resultStreams.add(currStream)
        }
        subsStreams = resultStreams
    }

    suspend fun getSearchStreams(request: String, subOnly: Boolean): List<Stream> =
        if (subOnly) {
            getSubscribedStreams().filter {
                it.name.lowercase().contains(request.lowercase())
            }
        }
        else
            getAllStreams().filter {
                it.name.lowercase().contains(request.lowercase())
            }

    fun setTopicMsgCount(topicName: String, count: Int, streamName: String) {
        val topic = streams.firstOrNull { it.name == streamName }?.topics?.firstOrNull { it.name == topicName }
        val topicSubs = subsStreams.firstOrNull { it.name == streamName }?.topics?.firstOrNull() { it.name == topicName }
        topic?.msgCount = count
        topicSubs?.msgCount = count
    }
}

private fun List<com.example.homework_2.network.networkModels.topics.Topic>.toTopicList(
    streamId: String,
    streamName: String
): List<Topic> {
    val result = mutableListOf<Topic>()
    for (topic in this) {
        val currTopic = Topic(
            name = topic.name,
            msgCount = 0,
            parentId = streamId,
            id = streamId + topic.name,
            parentName = streamName
        )
        result.add(currTopic)
    }
    return result
}