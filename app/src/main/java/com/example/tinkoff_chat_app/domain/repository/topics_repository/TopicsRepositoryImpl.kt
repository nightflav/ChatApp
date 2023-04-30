package com.example.tinkoff_chat_app.domain.repository.topics_repository

import com.example.tinkoff_chat_app.data.topics.TopicDao
import com.example.tinkoff_chat_app.models.data_transfer_models.TopicDto
import com.example.tinkoff_chat_app.network.ChatApi
import com.example.tinkoff_chat_app.utils.Resource
import com.example.tinkoff_chat_app.utils.TopicMappers.toDatabaseTopicModel
import com.example.tinkoff_chat_app.utils.TopicMappers.toTopicDtoList
import javax.inject.Inject

class TopicsRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val topicDao: TopicDao,
) : TopicsRepository {

    override suspend fun loadAllTopicsOfCurrentStream(
        streamId: Int, shouldFetch: Boolean, streamName: String
    ): Resource<List<TopicDto>> {

        val data = topicDao.getTopics(fromStream = streamId).toTopicDtoList()

        val result = if (shouldFetch) {
            try {
                val networkData =
                    chatApi.getTopics(streamId).body()!!.topics.toTopicDtoList(streamId, streamName)
                topicDao.deleteAllTopicsOfCurrentStream(streamId)
                topicDao.addTopics(networkData.map { it.toDatabaseTopicModel() })
                Resource.Success(data = networkData)
            } catch (e: Exception) {
                if (data.isEmpty()) Resource.Error(error = e)
                else Resource.Success(data = data)
            }
        } else if (data.isEmpty()) Resource.Error(error = IllegalStateException())
        else Resource.Success(data = data)

        return result
    }
}