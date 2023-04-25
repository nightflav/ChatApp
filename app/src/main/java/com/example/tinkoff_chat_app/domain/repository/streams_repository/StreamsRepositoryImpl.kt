package com.example.tinkoff_chat_app.domain.repository.streams_repository

import com.example.tinkoff_chat_app.data.streams.DatabaseStreamModel
import com.example.tinkoff_chat_app.data.streams.DatabaseSubscribedStreamModel
import com.example.tinkoff_chat_app.data.streams.StreamDao
import com.example.tinkoff_chat_app.data.topics.DatabaseTopicModel
import com.example.tinkoff_chat_app.data.topics.TopicDao
import com.example.tinkoff_chat_app.models.stream_screen_models.StreamModel
import com.example.tinkoff_chat_app.models.stream_screen_models.StreamScreenItem
import com.example.tinkoff_chat_app.models.stream_screen_models.TopicModel
import com.example.tinkoff_chat_app.network.ChatApi
import com.example.tinkoff_chat_app.utils.toTopicList
import javax.inject.Inject

class StreamsRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi, private val streamDao: StreamDao, private val topicDao: TopicDao
) : StreamsRepository {

    private var lastReadFromSubDB = true
    private var lastReadFromDB = true

    private var streams: MutableList<StreamModel>? = null
    private var subsStreams: MutableList<StreamModel>? = null
    private val topicsByStream: MutableMap<String, List<TopicModel>> = mutableMapOf()

    override suspend fun getAllStreamsLocal(): List<StreamScreenItem> {
        val localStreams = streamDao.getAllStreams().toStreamList()
        streams = localStreams.toMutableList()
        lastReadFromDB = true
        return streams!!
    }

    override suspend fun getSubscribedStreamsLocal(): List<StreamScreenItem> {
        val localStreams = streamDao.getSubStreams().toStreamList()
        subsStreams = localStreams.toMutableList()
        lastReadFromSubDB = true
        return subsStreams!!
    }

    override suspend fun getAllStreamsNetwork(): MutableList<StreamModel> {
        if (lastReadFromDB)
            loadStreams()
        return streams!!
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
        lastReadFromDB = false
    }

    override suspend fun getSubscribedStreamsNetwork(): MutableList<StreamModel> {
        if (lastReadFromSubDB)
            loadSubscriptions()
        return subsStreams!!
    }

    private suspend fun loadSubscriptions() {
        val resultStreams = mutableListOf<StreamModel>()
        val streamsNetwork = chatApi.getSubscriptions().body()?.subscriptions ?: emptyList()
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
        lastReadFromSubDB = false
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
            val newStream =
                stream.copy(
                    isSelected = !stream.isSelected,
                    topics = getStreamTopics(from = stream) ?: emptyList()
                )
            streamListToChange[streamListToChange.map { it.name }.indexOf(stream.name)] = newStream

            if (showSubscribed)
                streamDao.addStream(newStream.toDBSubStream())
            else
                streamDao.addStream(newStream.toDBStream())
        } else return
    }

    private suspend fun getStreamTopics(from: StreamModel): List<TopicModel>? {
        val topics = if (topicsByStream[from.name].isNullOrEmpty())
            try {
                val topicsNetwork =
                    chatApi.getTopics(from.id).body()?.topics?.toTopicList(from.id, from.name)
                for (topic in topicsNetwork!!)
                    topicDao.addTopic(topic.toDBTopic())
                topicsByStream[from.name] = topicsNetwork
                topicsNetwork
            } catch (_: Exception) {
                topicDao.getTopics(from.id).toTopicList()
            } else {
            topicsByStream[from.name]
        }
        return topics
    }

    private suspend fun List<DatabaseStreamModel>.toStreamList() = this.map {
        StreamModel(
            name = it.name,
            id = it.id,
            isSelected = it.isSelected,
            topics = topicDao.getTopics(it.name).toTopicList()
        )
    }

    private fun List<DatabaseTopicModel>?.toTopicList() = this?.map {
        TopicModel(
            name = it.name,
            msgCount = it.msgCount,
            parentId = it.parentId,
            id = it.id,
            parentName = it.parentName
        )
    }

    private fun StreamModel.toDBStream() = DatabaseStreamModel(
        name = this.name, id = this.id, isSelected = this.isSelected
    )

    private fun StreamModel.toDBSubStream() = DatabaseSubscribedStreamModel(
        name = this.name, id = this.id, isSelected = this.isSelected
    )

    private fun TopicModel.toDBTopic() = DatabaseTopicModel(
        id = id,
        name = name,
        msgCount = msgCount,
        parentId = parentId,
        parentName = parentName
    )
}


