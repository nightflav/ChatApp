package com.example.homework_2.datasource

import com.example.homework_2.messages
import com.example.homework_2.models.Stream
import com.example.homework_2.models.Topic
import com.example.homework_2.streams
import kotlinx.coroutines.delay

object StreamDatasource {

    suspend fun getAllStreams(): MutableList<Stream> {
        delay(200L)
        return editedStreams
    }

    suspend fun getSearchStreams(request: String): MutableList<Stream> {
        delay(200L)
        val resultStreams = mutableListOf<Stream>()
        editedStreams.forEach {
            if (it.name.contains(request))
                resultStreams.add(it)
        }
        return resultStreams
    }

    fun containsTopic(topicId: String) = messages.containsKey(topicId)

    private val editedStreams: MutableList<Stream> by lazy { streams.toMutableList() }

    fun getTopicById(topicId: String): Topic? {
        for (stream in streams)
            for (topic in stream.topics)
                if (topic.id == topicId)
                    return topic

        return null
    }

    fun getStreamById(streamId: String): Stream? {
        for (stream in streams)
            if (stream.id == streamId)
                return stream

        return null
    }
}